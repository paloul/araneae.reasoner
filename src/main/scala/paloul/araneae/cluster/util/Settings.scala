package paloul.araneae.cluster.util

import akka.actor._
import com.typesafe.config.Config

/**
 * This companion object here should not be touched, its basic infrastructure support
 * to help create a connection between our application.conf file, Settings class
 * and the Actor System.
 */
object Settings extends ExtensionId[Settings] with ExtensionIdProvider {

  // The apply method is a scala way of working with
  // companion object and instantiation of classes
  def apply(config: Config): Settings = new Settings(config)

  // The lookup method is required by ExtensionIdProvider,
  // so we return ourselves here, this allows us
  // to configure our extension to be loaded when
  // the ActorSystem starts up
  override def lookup: Settings.type = Settings

  // This method will be called by Akka to instantiate our Extension
  override def createExtension(system: ExtendedActorSystem): Settings = apply(system.settings.config)

  // Needed to get the type right when used from Java
  override def get(system: ActorSystem): Settings = super.get(system)
}

/**
 * Settings class to help parse application.conf and make values available
 * during runtime of application. If you want something from app.conf
 * available in application then add objects and parsing logic here
 *
 * @param config The reference to config parameters i.e. the application.conf
 */
class Settings(val config: Config) extends Extension {

  import scala.concurrent.duration._
  import scala.jdk.CollectionConverters._

  def this(system: ExtendedActorSystem) = this(system.settings.config)

  private val p = getClass.getPackage

  object application {
    val name: String = p.getImplementationTitle
    val version: String = p.getImplementationVersion

    // Holds config params from application.conf concerning the Araneae App settings
    val akkaClusterName: String = config.getString("application.akka-cluster-name")
    val akkaRemotingPort: Int = config.getInt("application.akka-remoting-port")
    val akkaAskTimeout: Duration = Duration(config.getString("application.akka-ask-timeout"))
    val akkaSeedHost: String = config.getString("application.akka-seed-host")
    val akkaSeedPort: Int = config.getInt("application.akka-seed-port")
    val akkaHttpHost: String = config.getString("application.akka-http-host")
    val akkaHttpPort: Int = config.getInt("application.akka-http-port")
    val cloudDeploy: Boolean = config.getBoolean("application.cloud-deploy")
  }

  object kafka_processor {
    object entity {
      val servers = config.getString("application.kafka-processor.entity.servers")
      val topic = config.getString("application.kafka-processor.entity.topic")
      val group = config.getString("application.kafka-processor.entity.group")
    }

    // You can add settings for more specialized data producers as well, i.e. cameras, drones, cars.  
  }

  // ******************************************************************************
  // Any additional custom settings can be added here.
  // Anything added here should also reside in the application.conf file (if you intend to use it - obviously)

  // ******************************************************************************

}

