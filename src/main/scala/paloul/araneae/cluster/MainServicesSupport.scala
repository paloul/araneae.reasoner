package paloul.araneae.cluster

import akka.actor.typed.ActorRef
import org.slf4j.{Logger, LoggerFactory}

/**
 * Contains the supporting behaviors that run under the root actor context
 */
trait MainServicesSupport {

  sealed trait Command
  case object NodeMemberUp extends Command
  final case class ShardingStarted(region: ActorRef[UserEvents.Command]) extends Command
  final case class BindingFailed(reason: Throwable) extends Command

  private val log: Logger = LoggerFactory.getLogger("MainServicesSupport")

}
