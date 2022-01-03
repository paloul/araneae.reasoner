package paloul.araneae.reasoner

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement
import com.typesafe.config.ConfigFactory
import paloul.araneae.reasoner.rest.RestServicesSupport

import scala.concurrent.Future

object Main extends MainServicesSupport with MainSettingsSupport {

  def init(settings: Settings): Unit = {

  }

  def main(args: Array[String]): Unit = {

    // Load the application.conf file and create our own Settings helper class
    val settings: Settings = settings

    init(settings)

    // TODO: Delete all below. No need after initServices is implemented

    // Root Behavior is the entry point for the whole akka cluster. It is the main behavior (actor)
    // that all others spawn from, i.e. Cluster Sharding, Akka-Http APIs, etc.
    // TODO: Define rootBehavior

    // Start the Actor System
    val system = ActorSystem[Nothing](rootBehavior, settings.application.akkaClusterName, settings.config)

    // If we are deploying to cloud then start the management server and cluster bootstrap services
    if (settings.application.cloudDeploy) {
      AkkaManagement(system).start()

      ClusterBootstrap(system).start()
    }
  }
}