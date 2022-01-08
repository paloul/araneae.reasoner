package paloul.araneae.cluster

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import org.slf4j.{Logger, LoggerFactory}
import paloul.araneae.cluster.util.Settings

/**
 * Supporting the Main class with added root Service behaviors
 */
trait MainServicesSupport {

  //************************************************************************************
  // Root Level Custom Messages
  sealed trait Command
  case object NodeMemberUp extends Command
  final case class ShardingStarted(region: ActorRef[UserEvents.Command]) extends Command
  final case class BindingFailed(reason: Throwable) extends Command
  //************************************************************************************

  private val log: Logger = LoggerFactory.getLogger("MainServicesSupport")

  def starting(ctx: ActorContext[Command],
               sharding: Option[ActorRef[UserEvents.Command]],
               joinedCluster: Boolean,
               settings: Settings
              ): Behavior[Command] = Behaviors.receive[Command] {

  }

}
