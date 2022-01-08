package paloul.araneae.cluster

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.typed.{Cluster, SelfUp, Subscribe}
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement
import paloul.araneae.cluster.util.Settings

object Main extends MainServicesSupport with MainSettingsSupport {

  /**
   * Initialize the Actor System
   * @param settings
   */
  def init(settings: Settings): Unit = {
    ActorSystem(
      Behaviors.setup[Command] { ctx =>
        // If Deploy to Cloud is defined true,
        // then enable management and bootstrap services
        if (settings.application.cloudDeploy) {
          AkkaManagement(ctx.system).start()
          ClusterBootstrap(ctx.system).start()
        }

        val cluster = Cluster(ctx.system)
        val upAdapter = ctx.messageAdapter[SelfUp](_ => NodeMemberUp)
        cluster.subscriptions ! Subscribe(upAdapter, classOf[SelfUp])

        starting(ctx, None, joinedCluster = false, settings)
      },
      settings.application.akkaClusterName,
      settings.config
    )
  }

  def main(args: Array[String]): Unit = {

    // Load the application.conf file and create our own Settings helper class
    val settings: Settings = settings

    //init(settings)

    // TODO: Delete all below. No need after initServices is implemented

    // Root Behavior is the entry point for the whole akka cluster. It is the main behavior (actor)
    // that all others spawn from, i.e. Cluster Sharding, Akka-Http APIs, etc.
    // TODO: Define rootBehavior

    // Start the Actor System
    //val system = ActorSystem[Nothing](rootBehavior, settings.application.akkaClusterName, settings.config)

  }
}