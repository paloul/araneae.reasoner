package paloul.araneae.reasoner

import akka.actor.typed.ActorSystem
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement
import com.typesafe.config.ConfigFactory
import paloul.araneae.reasoner.rest.RestServicesSupport

object Main extends App with RestServicesSupport {

  // Load the application.conf file and create our own Settings helper class
  val preConfig = ConfigFactory.load()
  val settings: Settings = Settings(preConfig)

  // If we are deploying to cloud then assign the canonical host name to the
  // current IP address grabbed from the underlying OS support libs.
  // When deploying to cloud and enabling cluster management and cluster bootstrap,
  // the cluster seed nodes need to be cleared. The cluster seed nodes should only
  // be used when you actually know the IP of seed nodes before the applications start.
  // They are cleared so that cluster bootstrap can do its thing and discover nodes
  // on the same network as peers.
  def getRuntimeConfigMods: String = {
    if (!settings.application.cloudDeploy) return ""

    // Get the current IP of the host
    val localhost: java.net.InetAddress = java.net.InetAddress.getLocalHost
    val localIpAddress: String = localhost.getHostAddress

    "akka.remote.artery.canonical.hostname=" + localIpAddress + "\n" +
      "akka.management.http.hostname=" + localIpAddress + "\n" +
      "akka.cluster.seed-nodes = []" + "\n"
  }

  // Create new config by merging runtime live mods with the initial pre config loaded earlier
  val config = ConfigFactory.load(ConfigFactory.parseString(getRuntimeConfigMods).withFallback(preConfig))

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