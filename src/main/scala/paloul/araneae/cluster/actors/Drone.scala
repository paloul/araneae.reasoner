package paloul.araneae.cluster.actors

import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.cluster.sharding.external.ExternalShardAllocationStrategy
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity}
import akka.kafka.cluster.sharding.KafkaClusterSharding
import paloul.araneae.cluster.util.Settings
import paloul.araneae.cluster.util.serializers.CborSerializable

import scala.concurrent.Future
import scala.concurrent.duration._

object Drone {

  //************************************************************************************
  // Drone Messages

  /** Base Message type for all incoming Drone messages */
  sealed trait Command extends CborSerializable {
    def droneId: String
  }

  /** Base Message type for all outgoing Drone messages */
  sealed trait Reply extends CborSerializable
  //************************************************************************************

  /**
   * Instantiates a new Drone given the id. Only accessible from companion object
   * @param id
   * @return
   */
  private def apply(id: String): Behavior[Command] =
    Behaviors.setup { context =>
      Behaviors.withTimers { timers =>
        // Instantiate a new Drone with initial behavior set to inactive
        new Drone(id, context, timers).inactive(0)
      }
    }

  /**
   * Initialize the cluster sharding mechanism for Drone actors
   * @param system
   * @param settings
   * @return
   */
  def shardingInit(system: ActorSystem[_], settings: Settings): Future[ActorRef[Command]] = {
    import system.executionContext

    // Create the Kafka Message Extractor for sharded kafka clustering
    KafkaClusterSharding(system).messageExtractorNoEnvelope(
      timeout = FiniteDuration(settings.application.akkaAskTimeout.length, settings.application.akkaAskTimeout.unit),
      topic = settings.kafka_processor.drone.topic,
      entityIdExtractor = (msg: Command) => msg.droneId, // The entity id is the drone id inside the message for target recipient
      settings = settings.kafka_processor.kafkaConsumerSettings(
        system, settings.kafka_processor.drone.servers, settings.kafka_processor.drone.group)
    ).map(messageExtractor => {
      system.log.info("Kafka Message Extractor created. Initializing sharding for Drones...")

      // Create and initialize the Cluster Sharding behavior to create a Drone instance controlled by Shard Manager
      // Establish an external shard allocation strategy that uses the message extractor created above
      ClusterSharding(system).init(
        Entity(settings.kafka_processor.drone.entityTypeKey)(createBehavior = entityContext => Drone(entityContext.entityId))
          .withAllocationStrategy(new ExternalShardAllocationStrategy(system, settings.kafka_processor.drone.entityTypeKey.name))
          .withMessageExtractor(messageExtractor)
      )
    })
  }
}

/**
 * Actual Drone class that can only be created with the companion object. The constructor parameters are immutable
 * instance fields and are accessible from member methods. This still follows the functional actor behavior style.
 * @param context
 * @param timers
 */
class Drone private (id: String,
                     context: ActorContext[Drone.Command],
                     timers: TimerScheduler[Drone.Command]) {

  import Drone._

  private def inactive(n: Int): Behavior[Command] =
    Behaviors.receiveMessage {
      _ =>
        context.log.info("Drone[{}] received a message", id)
        Behaviors.same
    }

}
