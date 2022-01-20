package paloul.araneae.cluster

import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Terminated}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.cluster.typed.{Cluster, SelfUp, Subscribe}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement
import paloul.araneae.cluster.actors.Drone
import paloul.araneae.cluster.processors.DroneKafkaProcessor
import paloul.araneae.cluster.protobuf.DroneServiceHandler
import paloul.araneae.cluster.services.grpc.DroneGrpcService
import paloul.araneae.cluster.util.Settings

import scala.concurrent.Future
import scala.util.{Failure, Success}

//************************************************************************************
// Root Level Custom Messages
sealed trait Command
case object NodeMemberUp extends Command
final case class ShardingStarted(region: ActorRef[Drone.Command]) extends Command
final case class BindingFailed(reason: Throwable) extends Command
//************************************************************************************

/**
 * Supporting the Main class with added root Service behaviors
 */
trait MainSupportDrones {

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

  /**
   * State of behavior that monitors the initialization of this actor system joining a cluster and starting sharding
   * @param context Reference to Actor Context
   * @param sharding Reference to Sharding Manager
   * @param joinedCluster Boolean if actor system has joined cluster
   * @param settings Reference to Settings for access to configuration env variables
   * @return
   */
  private def starting(context: ActorContext[Command],
                       sharding: Option[ActorRef[Drone.Command]],
                       joinedCluster: Boolean,
                       settings: Settings
                      ): Behavior[Command] = Behaviors.receive[Command] {

      case (context, ShardingStarted(region)) if joinedCluster =>
        context.log.info("Sharding has started")
        start(context, region, settings)
      case (_, ShardingStarted(region)) =>
        context.log.info("Sharding has started")
        starting(context, Some(region), joinedCluster, settings)
      case (context, NodeMemberUp) if sharding.isDefined =>
        context.log.info("Member has joined the cluster")
        start(context, sharding.get, settings)
      case (_, NodeMemberUp) =>
        context.log.info("Member has joined the cluster")
        starting(context, sharding, joinedCluster = true, settings)

  }

  /**
   * State of behavior that is activated after cluster is joined and sharding has started. Starts grpc services.
   * @param context Reference to Actor Context
   * @param region Reference to Sharding Manager
   * @param settings Reference to Settings for access to configuration env variables
   * @return After initiate start state, returns the running behavior
   */
  private def start(context: ActorContext[Command],
                    region: ActorRef[Drone.Command],
                    settings: Settings
                   ): Behavior[Command] = {

    import context.executionContext

    context.log.info("Sharding started and joined the cluster. Starting Drone's Kafka Processor")

    val droneKafkaProcessor = context.spawn[Nothing](DroneKafkaProcessor(region, settings), name="drone-kafka-processor")
    val grpcBinding: Future[Http.ServerBinding] = startGrpc(context.system, region, settings)

    grpcBinding.onComplete {
      case Failure(t) =>
        context.self ! BindingFailed(t)
      case _ =>
    }

    running(context, grpcBinding, droneKafkaProcessor)

  }

  /**
   *
   * @param context Reference to Actor Context
   * @param grpcBinding Reference to the GRPC ServerBinding Future
   * @param droneKafkaProcessor Reference to the behavior running from DroneKafkaProcessor
   * @return A Behavior of type Command to handle running state, waiting for termination signal
   */
  private def running(context: ActorContext[Command],
                      grpcBinding: Future[Http.ServerBinding],
                      droneKafkaProcessor: ActorRef[Nothing]): Behavior[Command] =
    Behaviors.receiveMessagePartial[Command] {
      case BindingFailed(t) =>
        context.log.error("Failed to bind the grpc front end", t)

        Behaviors.stopped
    }.receiveSignal {
      case (context, Terminated(`droneKafkaProcessor`)) =>
        context.log.warn("Kafka event processor stopped. Shutting down")

        grpcBinding.map(_.unbind())(context.executionContext)
        Behaviors.stopped
    }

  /**
   *
   * @param system Reference to typed Actor System
   * @param region Reference to Sharding Manager
   * @param settings Reference to Settings for access to configuration env variables
   * @return A Future of type ServerBinding, which will determine if service successfully bound
   */
  private def startGrpc(system: ActorSystem[_],
                        region: ActorRef[Drone.Command],
                        settings: Settings): Future[Http.ServerBinding] = {

    // Create service handlers
    val service: HttpRequest => Future[HttpResponse] =
      DroneServiceHandler(new DroneGrpcService(system, region, settings))(system.classicSystem)

    // Bind service handler servers and return the binding itself as a future
    Http()(system.classicSystem).newServerAt(
      settings.application.akkaHttpHost, settings.application.akkaHttpPort).bind(service)
  }
}
