package paloul.araneae.cluster.services.grpc

import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.{ActorRef, ActorSystem, Scheduler}
import akka.util.Timeout
import paloul.araneae.cluster.actors.Drone
import paloul.araneae.cluster.util.Settings
import paloul.araneae.cluster.protobuf.{DroneService, DroneStateRequest, DroneStateResponse}

import scala.concurrent.{ExecutionContextExecutor, Future}

/**
 * Implementation of the GRPC Drone Service trait defined in Protobuf file Drones.proto
 * @param system Typed Actor System reference
 * @param shardRegion Drone Shard Region Manager reference
 * @param settings Reference to Settings for access to configuration env variables
 */
class DroneGrpcService(system: ActorSystem[_], shardRegion: ActorRef[Drone.Command], settings: Settings) extends DroneService {

  implicit val scheduler: Scheduler = system.scheduler
  implicit val ec: ExecutionContextExecutor = system.executionContext
  implicit val timeout: Timeout = Timeout(settings.application.akkaAskTimeout.length, settings.application.akkaAskTimeout.unit)

  /**
   * Implementation of the DroneState GRPC Service in protobuf file Drones.proto
   * @param in The protobuf message for requesting drone state
   * @return A future encapsulating a DroneStateResponse message
   */
  override def droneState(in: DroneStateRequest): Future[DroneStateResponse] = {
    shardRegion
      .ask[Drone.DroneState](replyTo => Drone.GetDroneState(in.id, replyTo))
      .map(droneState => DroneStateResponse(in.id, health = droneState.health, battery = droneState.battery))
  }
}
