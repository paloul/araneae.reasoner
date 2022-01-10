package paloul.araneae.cluster

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.typed.{Cluster, SelfUp, Subscribe}
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement
import paloul.araneae.cluster.util.Settings

object Main extends MainServicesSupport with MainSettingsSupport {

  /**
   * Initialize the Actor System and setup root behaviors using ServicesSupport trait
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

  /**
   * Main Entry point of the application
   */
  def main(args: Array[String]): Unit = {

    // Load the application.conf file and create our own Settings helper class
    val settings: Settings = settings

    // Pass the dynamic settings over to init method
    init(settings)

  }
}