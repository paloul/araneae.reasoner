package paloul.araneae.cluster

import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.cluster.typed.{Cluster, SelfUp, Subscribe}
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement
import org.slf4j.{Logger, LoggerFactory}
import paloul.araneae.cluster.actors.Drone
import paloul.araneae.cluster.util.Settings

import scala.util.{Failure, Success}

/**
 * Supporting the Main class with added root Service behaviors
 */
trait MainSupportDrones {

  //************************************************************************************
  // Root Level Custom Messages
  sealed trait Command
  case object NodeMemberUp extends Command
  final case class ShardingStarted(region: ActorRef[Drone.Command]) extends Command
  final case class BindingFailed(reason: Throwable) extends Command
  //************************************************************************************

  private val log: Logger = LoggerFactory.getLogger("MainServicesSupport")

  /**
   * Initialize the Actor System and setup root behaviors using ServicesSupport trait
   * @param settings Settings loaded with values from application.conf
   */
  def initDrones(settings: Settings): Unit = {
    ActorSystem(
      Behaviors.setup[Command] { context =>
        // If Deploy to Cloud is defined true,
        // then enable management and bootstrap services
        if (settings.application.cloudDeploy) {
          AkkaManagement(context.system).start()
          ClusterBootstrap(context.system).start()
        }

        val cluster = Cluster(context.system)
        val upAdapter = context.messageAdapter[SelfUp](_ => NodeMemberUp)
        cluster.subscriptions ! Subscribe(upAdapter, classOf[SelfUp])

        context.pipeToSelf(Drone.shardingInit(context.system, settings)) {
          case Success(extractor) => ShardingStarted(extractor)
          case Failure(ex) => throw ex
        }

        starting(context, None, joinedCluster = false, settings)
      },
      settings.application.akkaClusterName,
      settings.config
    )
  }

  private def starting(context: ActorContext[Command],
               sharding: Option[ActorRef[Drone.Command]],
               joinedCluster: Boolean,
               settings: Settings
              ): Behavior[Command] = Behaviors.receive[Command] {

  }

}
