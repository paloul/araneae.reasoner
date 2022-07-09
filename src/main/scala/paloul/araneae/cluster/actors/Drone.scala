package paloul.araneae.cluster.actors

import akka.Done
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.cluster.sharding.typed.ShardingMessageExtractor
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity, EntityTypeKey}
import paloul.araneae.cluster.util.Settings
import paloul.araneae.cluster.util.serializers.ProtoSerializable

import scala.concurrent.Future
import scala.concurrent.duration._

object Drone {

  //************************************************************************************
  // Drone Messages

  /** Base Message type for all incoming Drone messages */
  sealed trait Command extends ProtoSerializable {
    def droneId: String
  }

  /** Commands */
  // The Stop command doesnt need a unique id. The message is sent locally within jvm from
  // Shard Manager on same node to Drone instance on same node by direct actor reference.
  final case class Stop(droneId: String = "Stop") extends Command

  final case class SetDroneState(droneId: String, health: Int, battery: Int, replyTo: ActorRef[Done]) extends Command
  final case class GetDroneState(droneId: String, replyTo: ActorRef[DroneState]) extends Command

  final case class SetDroneLocation(droneId: String, lat: Int, long: Int, replyTo: ActorRef[Done]) extends Command
  final case class GetDroneLocation(droneId: String, replyTo: ActorRef[DroneLocation]) extends Command

  /** State */
  final case class DroneState(health: Int, battery: Int)
  final case class DroneLocation(lat: Int, lon: Int)
  //************************************************************************************

  // The typekey used in sharding to route to unique instances
  val TypeKey: EntityTypeKey[Drone.Command] = EntityTypeKey[Drone.Command]("Drone")

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
            DroneLocation(110,101)
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

    import scala.concurrent.ExecutionContext.Implicits.global

    // Initialize the shard
    system.log.info("Initializing sharding for Drones...")

    // Create a new Message Extractor without Enveloping. This removes the need to
    // wrap messages with the so-called Shard
    val noEnvelopeMessageExtractor = ShardingMessageExtractor.noEnvelope[Command](
      numberOfShards = settings.akka.cluster.sharding.numberOfShards,
      stopMessage = Stop()
    ) (extractEntityId = (msg: Command) => msg.droneId)

    // Create and initialize the Cluster Sharding behavior to create a Drone instance controlled by Shard Manager
    // Establish an external shard allocation strategy that uses the message extractor created above
    Future {
      ClusterSharding(system).init(
        Entity(TypeKey)(createBehavior = entityContext => Drone(entityContext.entityId))
          .withMessageExtractor(noEnvelopeMessageExtractor)
      )
    }
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
        context.log.info("Requested state for Drone[{}]", droneId)
        replyTo ! droneState
        Behaviors.same
      case GetDroneLocation(droneId, replyTo) =>
        context.log.info("Requested location for Drone[{}]", droneId)
        replyTo ! droneLocation
        Behaviors.same
      case _ =>
        context.log.info("Drone[{}] received a message", id)
        Behaviors.same
    }

}
