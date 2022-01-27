package paloul.araneae.cluster.processors

import akka.Done
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Scheduler}
import akka.kafka.{CommitterSettings, ConsumerRebalanceEvent, Subscriptions}
import akka.kafka.cluster.sharding.KafkaClusterSharding
import akka.kafka.scaladsl.{Committer, Consumer}
import akka.pattern.retry
import akka.util.Timeout
import paloul.araneae.cluster.actors.Drone
import paloul.araneae.cluster.protobuf.{DroneLocationProto, DroneStateProto}
import paloul.araneae.cluster.util.Settings

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Try

object DroneKafkaProcessor {

  //************************************************************************************
  // DroneKafkaProcessor Messages

  /** Base Message type messages */
  sealed trait Command

  // Internal notification message to self to signal consumer stopped
  private case class KafkaConsumerStopped(reason: Try[Any]) extends Command
  //************************************************************************************

  /**
   * Initiate the Drone Kafka Processor. Setup a self-listener to watch when underlying
   * consumer stream is shutdown.
   * @param shardRegion Drone Shard Region Manager reference
   * @param settings Reference to Settings for access to configuration env variables
   * @return
   */
  def apply(shardRegion: ActorRef[Drone.Command], settings: Settings): Behavior[Nothing] = {
    Behaviors
      .setup[Command] { context =>
        val result = startConsumingFromTopic(context.system, shardRegion, settings)

        // Pipe the reply to self and catch why the consumer was stopped
        context.pipeToSelf(result) {
          result => KafkaConsumerStopped(result)
        }

        Behaviors.receiveMessage[Command] {
          case KafkaConsumerStopped(reason) =>
            context.log.info("Consumer stopped {}", reason)
            Behaviors.stopped
        }

      }.narrow
  }

  /**
   * Start the stream and consume from the Drone topics. Send messages incoming over topics to the target Drone
   * via the Shard Region actor reference provided.
   * @param system Implicit typed Actor System. It is marked as implicit to be used with the Source/Sink
   *               Akka Stream Materializer for the runWith command. The following link about the Actor
   *               Materializer Lifecycle provides more background:
   *               https://doc.akka.io/docs/akka/current/stream/stream-flows-and-basics.html#actor-materializer-lifecycle.
   * @param shardRegion Drone Shard Region Manager reference
   * @param settings Reference to Settings for access to configuration env variables
   * @return
   */
  private def startConsumingFromTopic(implicit system: ActorSystem[_],
                                      shardRegion: ActorRef[Drone.Command],
                                      settings: Settings): Future[Done] = {

    // obtain an Akka ActorRef that will handle consumer group rebalance events
    val rebalanceListener: ActorRef[ConsumerRebalanceEvent] =
      KafkaClusterSharding(system).rebalanceListener(settings.kafka_processor.drone.entityTypeKey)

    // Need to convert the rebalance listener to a classic ActorRef until Alpakka Kafka supports Akka Typed
    import akka.actor.typed.scaladsl.adapter._
    val subscription = Subscriptions
      .topics(settings.kafka_processor.drone.topics: _*)
      .withRebalanceListener(rebalanceListener.toClassic)

    // Get kafka consumer settings for Drones
    val kafkaConsumerSettings = settings.kafka_processor.kafkaConsumerSettings(
      system, settings.kafka_processor.drone.servers, settings.kafka_processor.drone.group
    )

    // Time out for sending the ask message to actor
    val askTimeout: Timeout = Timeout(
      settings.application.akkaAskTimeout.length, settings.application.akkaAskTimeout.unit)

    // Create the source and connect it via sink
    Consumer.sourceWithOffsetContext(kafkaConsumerSettings, subscription)
      // MapAsync with Retry is an Akka Stream with flow control and backpressure
      // The message is sent as an Ask in order to receive an Acknowledgement and thus feedback
      // for the backpressure mechanism. At most parallelism=20 future can be active. If that
      // number of active futures is surpassed, backpressure will activate on the stream.
      .mapAsync(20) { record =>

        system.log.info(s"Drone Id consumed message from Kafka partition ${record.key()}->${record.partition()}")

        retry(() =>
          shardRegion.ask[Done](replyTo => {

            // Determine which topic the message belongs to
            record.topic() match {
              case "drone.state" =>
                val droneStateProto = DroneStateProto.parseFrom(record.value())
                Drone.SetDroneState(
                  droneStateProto.id,
                  droneStateProto.health,
                  droneStateProto.battery,
                  replyTo
                )

              case "drone.location" =>
                val droneLocationProto = DroneLocationProto.parseFrom(record.value())
                Drone.SetDroneLocation(
                  droneLocationProto.id,
                  droneLocationProto.lat,
                  droneLocationProto.lon,
                  replyTo
                )

              case _ => null

            }

          })(askTimeout, system.scheduler),
          attempts = 5, // Retry 5 times before giving up
          delay = 1.second // Retry with 1 second delay between messages
        )(system.executionContext, system.classicSystem.scheduler)
      }
      .runWith(Committer.sinkWithOffsetContext(CommitterSettings(system)))
  }
}
