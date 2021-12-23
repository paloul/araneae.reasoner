package paloul.araneae.reasoner

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement
import com.typesafe.config.ConfigFactory
import paloul.araneae.reasoner.rest.RestServicesSupport

import scala.concurrent.Future

object Main {

  /**
   * Based on Environment Variables defined in app.conf and loaded via Settings, dynamically
   * generates a Config string required to work with the Cloud Deploy feature which starts up
   * things like Akka Management and Akka Cluster Bootstrap/Discovery.
   *
   * @param cloudDeploy A bool from Settings that determines if deployment intended for cloud.
   * @param configStr Initial String Config representation that will be concatenated with dynamic updates
   * @return String containing config updates for Akka Remote & Management Hostnames and cleared Seed Nodes
   */
  def runtimeCloudDeployMods(cloudDeploy: Boolean, configStr: String = ""): String = {
    // If we are deploying to cloud then assign the canonical host name to the
    // current IP address grabbed from the underlying OS support libs.
    // When deploying to cloud and enabling cluster management and cluster bootstrap,
    // the cluster seed nodes need to be cleared. The cluster seed nodes should only
    // be used when you actually know the IP of seed nodes before the applications start.
    // They are cleared so that cluster bootstrap can do its thing and discover nodes
    // on the same network as peers.

    // Get the current IP of the host
    val localhost: java.net.InetAddress = java.net.InetAddress.getLocalHost
    val localIpAddress: String = localhost.getHostAddress

    if (cloudDeploy)
      configStr.concat(
      s"""
         |akka.remote.artery.canonical.hostname = $localIpAddress
         |akka.management.http.hostname = $localIpAddress
         |akka.cluster.seed-nodes = []
         |""".stripMargin
      )
    else
      configStr
  }

  /**
   * Generate Settings class that can be used throughout the application. Provides quick
   * access to environment variables during runtime execution. Encapsulates Akka's Config class.
   *
   * @return A final representation of settings with all env vars and configs updated dynamically
   */
  def settings(): Settings = {
    // Load the application.conf file and create our own Settings helper class
    val preConfig = ConfigFactory.load() // pre config as some settings need to be updated during runtime
    val preSettings: Settings = Settings(preConfig) // pre settings as we'll create new settings with updated config

    // Final Settings from modded Config to reflect dynamic runtime updates. Settings encases Config in itself
    Settings(
      ConfigFactory.parseString(
        runtimeCloudDeployMods(preSettings.application.cloudDeploy)).withFallback(preConfig))
  }

  def startServices(system: ActorSystem[_], settings: Settings): Future[Http.ServerBinding] = {

  }

  def main(args: Array[String]): Unit = {

    // Load the application.conf file and create our own Settings helper class
    val settings: Settings = settings

    // Root Behavior is the entry point for the whole akka cluster. It is the main behavior (actor)
    // that all others spawn from, i.e. Cluster Sharding, Akka-Http APIs, etc.
    // TODO: Define rootBehavior

    // Start the Actor System
    val system = ActorSystem[Nothing](rootBehavior, settings.application.akkaClusterName, config)

    // If we are deploying to cloud then start the management server and cluster bootstrap services
    if (settings.application.cloudDeploy) {
      AkkaManagement(system).start()

      ClusterBootstrap(system).start()
    }
  }
}