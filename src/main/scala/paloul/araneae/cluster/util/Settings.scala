package paloul.araneae.cluster.util

import akka.actor._
import akka.actor.typed.ActorSystem
import com.typesafe.config.Config
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

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
  override def createExtension(system: akka.actor.ExtendedActorSystem): Settings = apply(system.settings.config)

  // Needed to get the type right when used from Java
  override def get(system: akka.actor.ActorSystem): Settings = super.get(system)
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
    import akka.kafka.ConsumerSettings
    import akka.cluster.sharding.typed.scaladsl.EntityTypeKey

    object drone {
      import paloul.araneae.cluster.actors.Drone

      val servers: String = config.getString("application.kafka-processor.drone.servers")
      val topics: List[String] = config.getStringList("application.kafka-processor.drone.topics").asScala.toList
      val group: String = config.getString("application.kafka-processor.drone.group")

      /**
       * Using the same consumer group id for the cluster sharding entity type key name, we can setup
       * multiple consumer groups and connect with a different sharded entity coordinator for each.
       */
      val entityTypeKey: EntityTypeKey[Drone.Command] = EntityTypeKey(group)
    }

    /**
     * Given the parameters, returns a Kafka Consumer Setting
     * @param system
     * @param servers
     * @param groupId
     * @return
     */
    def kafkaConsumerSettings(system: ActorSystem[_],
                              servers: String,
                              groupId: String,
                              ): ConsumerSettings[String, Array[Byte]] = {

      ConsumerSettings(system, new StringDeserializer, new ByteArrayDeserializer)
          .withBootstrapServers(servers)
          .withGroupId(groupId)
          .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
          .withStopTimeout(0.seconds)
    }

    // You can add settings for more specialized data producers as well, i.e. cameras, drones, cars.  
  }

  // ******************************************************************************
  // Any additional custom settings can be added here.
  // Anything added here should also reside in the application.conf file (if you intend to use it - obviously)

  // ******************************************************************************

}

