package paloul.araneae.cluster.actors

import akka.Done
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, PostStop}
import akka.cluster.sharding.typed.ShardingMessageExtractor
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity, EntityTypeKey}
import paloul.araneae.cluster.util.Settings
import paloul.araneae.cluster.util.serializers.ProtoSerializable

import scala.concurrent.Future
import scala.concurrent.duration._

object Agent {

  //************************************************************************************
  // Agent Messages

  /** Base Message type for all incoming Agent messages */
  sealed trait Command extends ProtoSerializable {
    def agentId: String
  }

  /** Commands */
  // The Stop command doesnt need a unique id. The message is sent locally within jvm from
  // Shard Manager on same node to Agent instance on same node by direct actor reference.
  final case class Stop(agentId: String = "Stop") extends Command

  final case class SetAgentState(agentId: String, health: Int, battery: Int, replyTo: ActorRef[Done]) extends Command
  final case class GetAgentState(agentId: String, replyTo: ActorRef[AgentState]) extends Command

  final case class SetAgentLocation(agentId: String, lat: Int, long: Int, replyTo: ActorRef[Done]) extends Command
  final case class GetAgentLocation(agentId: String, replyTo: ActorRef[AgentLocation]) extends Command

  /** State */
  final case class AgentState(health: Int, battery: Int)
  final case class AgentLocation(lat: Int, lon: Int)
  //************************************************************************************

  // The typekey used in sharding to route to unique instances
  val TypeKey: EntityTypeKey[Agent.Command] = EntityTypeKey[Agent.Command]("Agent")

  /**
   * Instantiates a new Agent given the id. Only accessible from companion object
   * @param id A unique string representing a unique agent instance
   * @return The Behavior reference to an initial inactive state handler of a new Agent instance
   */
  private def apply(id: String): Behavior[Command] =
    Behaviors.setup { context =>
      Behaviors.withTimers { timers =>
        // Instantiate a new Agent with initial behavior set to inactive and default state values
        new Agent(id, context, timers)
          .inactive(
            AgentState(100,100),
            AgentLocation(110,101)
          )
      }
    }

  /**
   * Initialize the cluster sharding mechanism for Agent actors
   * https://doc.akka.io/docs/alpakka-kafka/current/cluster-sharding.html
   * @param system Reference to Akka System
   * @param settings Reference to Settings for access to configuration env variables
   * @return An Akka Cluster Shard Manager actor reference able to receive Agent.Command messages
   */
  def shardingInit(system: ActorSystem[_], settings: Settings): Future[ActorRef[Command]] = {

    import scala.concurrent.ExecutionContext.Implicits.global

    // Initialize the shard
    system.log.info("Initializing sharding for Agents...")

    // Create a new Message Extractor without Enveloping. This removes the need to
    // wrap messages with the so-called Shard
    val noEnvelopeMessageExtractor = ShardingMessageExtractor.noEnvelope[Command](
      numberOfShards = settings.akka.cluster.sharding.numberOfShards,
      stopMessage = Stop()
    ) (extractEntityId = (msg: Command) => msg.agentId)

    // Create and initialize the Cluster Sharding behavior to create a Agent instance controlled by Shard Manager
    // Establish an external shard allocation strategy that uses the message extractor created above
    Future {
      ClusterSharding(system).init(
        Entity(TypeKey)(createBehavior = entityContext => Agent(entityContext.entityId))
          .withStopMessage(Stop())
          .withMessageExtractor(noEnvelopeMessageExtractor)
      )
    }
  }
}

/**
 * Actual Agent class that can only be created with the companion object. The constructor parameters are immutable
 * instance fields and are accessible from member methods. This still follows the functional actor behavior style.
 * https://doc.akka.io/docs/akka/current/typed/style-guide.html#functional-versus-object-oriented-style
 * https://doc.akka.io/docs/akka/current/typed/style-guide.html#passing-around-too-many-parameters
 * @param context The typed Actor Context
 * @param timers Reference to Time Scheduler that allows instance to send itself regular timed messages
 */
class Agent private (id: String,
                     context: ActorContext[Agent.Command],
                     timers: TimerScheduler[Agent.Command]) {

  // Import items defined inside companion object
  import Agent._

  private def inactive(agentState: AgentState, agentLocation: AgentLocation): Behavior[Command] =
    Behaviors.receive[Command] { (context, message) =>
      message match {
        case GetAgentState(agentId, replyTo) =>
          context.log.info("Requested state for Agent[{}]", agentId)
          replyTo ! agentState
          Behaviors.same
        case GetAgentLocation(agentId, replyTo) =>
          context.log.info("Requested location for Agent[{}]", agentId)
          replyTo ! agentLocation
          Behaviors.same
        case Stop(_) =>
          context.log.info("Agent[{}] received a Stop message. Stopping...", id)
          Behaviors.stopped
        case _ =>
          context.log.info("Agent[{}] received a message", id)
          // Clean up resources, tell children if any to stop as well
          Behaviors.same
      }
    }
    .receiveSignal {
      case (context, PostStop) =>
        context.log.info("Agent[{}] Stopped", id)
        // Any last second resource clean up should go here
        Behaviors.same
    }
}
