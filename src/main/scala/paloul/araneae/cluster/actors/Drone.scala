package paloul.araneae.cluster.actors

import akka.Done
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

  /** Commands */
  final case class SetDroneState(droneId: String, health: Int, battery: Int, replyTo: ActorRef[Done]) extends Command
  final case class GetDroneState(droneId: String, replyTo: ActorRef[DroneState]) extends Command

  final case class SetDroneLocation(droneId: String, lat: Int, long: Int, replyTo: ActorRef[Done]) extends Command
  final case class GetDroneLocation(droneId: String, replyTo: ActorRef[DroneLocation]) extends Command

  /** State */
  final case class DroneState(health: Int, battery: Int) extends CborSerializable
  final case class DroneLocation(lat: Int, lon: Int) extends CborSerializable
  //************************************************************************************

  /**
   * Instantiates a new Drone given the id. Only accessible from companion object
   * @param id A unique string representing a unique drone instance
   * @return The Behavior reference to an initial inactive state handler of a new Drone instance
   */
  private def apply(id: String): Behavior[Command] =
    Behaviors.setup { context =>
      Behaviors.withTimers { timers =>
        // Instantiate a new Drone with initial behavior set to inactive and default state values
        new Drone(id, context, timers)
          .inactive(
            DroneState(100,100),
            DroneLocation(0,0)
          )
      }
    }

  /**
   * Initialize the cluster sharding mechanism for Drone actors
   * https://doc.akka.io/docs/alpakka-kafka/current/cluster-sharding.html
   * @param system Reference to Akka System
   * @param settings Reference to Settings for access to configuration env variables
   * @return An Akka Cluster Shard Manager actor reference able to receive Drone.Command messages
   */
  def shardingInit(system: ActorSystem[_], settings: Settings): Future[ActorRef[Command]] = {
    import system.executionContext

    // Create the Kafka Message Extractor for sharded kafka clustering
    system.log.info("Creating Kafka Message Extractor...")
    KafkaClusterSharding(system).messageExtractorNoEnvelope(
      timeout = FiniteDuration(settings.application.akkaAskTimeout.length, settings.application.akkaAskTimeout.unit),
      // The head topic in topics is used merely to query Kafka cluster and retrieve number of partitions.
      // The number of partitions is used to create the underlying KafkaShardingNoEnvelopeExtractor
      // Important Note: Ensure all topics in the list are configured with equal partitions on the Kafka cluster
      topic = settings.kafka_processor.drone.topics.head,
      // The entity id is the drone id inside the message for target recipient
      entityIdExtractor = (msg: Command) => msg.droneId,
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
 * https://doc.akka.io/docs/akka/current/typed/style-guide.html#functional-versus-object-oriented-style
 * https://doc.akka.io/docs/akka/current/typed/style-guide.html#passing-around-too-many-parameters
 * @param context The typed Actor Context
 * @param timers Reference to Time Scheduler that allows instance to send itself regular timed messages
 */
class Drone private (id: String,
                     context: ActorContext[Drone.Command],
                     timers: TimerScheduler[Drone.Command]) {

  // Import items defined inside companion object
  import Drone._

  private def inactive(droneState: DroneState, droneLocation: DroneLocation): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetDroneState(droneId, replyTo) =>
        context.log.info("Requesting state for Drone[{}]", droneId)
        replyTo ! droneState
        Behaviors.same
      case GetDroneLocation(droneId, replyTo) =>
        context.log.info("Requesting location for Drone[{}]", droneId)
        replyTo ! droneLocation
        Behaviors.same
      case _ =>
        context.log.info("Drone[{}] received a message", id)
        Behaviors.same
    }

}
