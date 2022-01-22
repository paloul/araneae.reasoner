package paloul.araneae.cluster.processors

import akka.Done
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.kafka.{ConsumerRebalanceEvent, Subscriptions}
import akka.kafka.cluster.sharding.KafkaClusterSharding
import akka.kafka.scaladsl.Consumer
import paloul.araneae.cluster.actors.Drone
import paloul.araneae.cluster.util.Settings

import scala.concurrent.ExecutionContext
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

  def apply(shardRegion: ActorRef[Drone.Command], settings: Settings): Behavior[Nothing] = {
    Behaviors
      .setup[Command] { context =>
        val result = startConsumingFromTopic(context.system, shardRegion, settings)

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

  private def startConsumingFromTopic(system: ActorSystem[_],
                                      shardRegion: ActorRef[Drone.Command],
                                      settings: Settings): Future[Done] = {

    // obtain an Akka classic ActorRef that will handle consumer group rebalance events
    val rebalanceListener: ActorRef[ConsumerRebalanceEvent] =
      KafkaClusterSharding(system).rebalanceListener(settings.kafka_processor.drone.entityTypeKey)

    // Need to convert the rebalance listener to a classic ActorRef until Alpakka Kafka supports Akka Typed
    import akka.actor.typed.scaladsl.adapter._
    val subscription = Subscriptions
      .topics(settings.kafka_processor.drone.topic)
      .withRebalanceListener(rebalanceListener.toClassic)

    // Get kafka consumer settings for Drones
    val kafkaConsumerSettings = settings.kafka_processor.kafkaConsumerSettings(
      system, settings.kafka_processor.drone.servers, settings.kafka_processor.drone.group
    )

    Consumer.sourceWithOffsetContext(kafkaConsumerSettings, subscription)
      .mapAsync(20) { record =>



      }

  }

}
