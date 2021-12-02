package paloul.araneae.reasoner

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
 * @param config The reference to config parameters i.e. the application.conf
 */
class Settings(config: Config) extends Extension {

  import scala.concurrent.duration._

  def this(system: ExtendedActorSystem) = this(system.settings.config)

  private val p = getClass.getPackage

  object application {
    val name: String = p.getImplementationTitle
    val version: String = p.getImplementationVersion

    // Holds config params from application.conf concerning the Cluster App settings
    object cluster {
      val name: String = config.getString("application.cluster.name")
      val port: Int = config.getInt("application.cluster.port")
      val timeout: Duration = Duration(config.getString("application.cluster.timeout"))
      val deployCloud: Boolean = config.getBoolean("application.cluster.deploy-cloud")
      val maxNumNodes: Int = config.getInt("application.cluster.max-num-nodes")
    }

    // Holds config params from application.conf concerning the HTTP API settings
    object http {
      val host: String = config.getString("application.http.host")
      val port: Int = config.getInt("application.http.port")
    }
  }

  // ******************************************************************************
  // Any additional custom settings can be added here.
  // Anything added here should also reside in the application.conf file (if you intend to use it - obviously)

  // ******************************************************************************

}

