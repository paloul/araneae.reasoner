package paloul.araneae.cluster.services.grpc

import akka.NotUsed
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.{ActorRef, ActorSystem, Scheduler}
import akka.stream.scaladsl.{Sink, Source}
import akka.util.Timeout
import paloul.araneae.cluster.actors.Drone
import paloul.araneae.cluster.util.Settings
import paloul.araneae.cluster.protobuf.{DroneLocation, DroneLocationRequest, DroneService, DroneState, DroneStateRequest}

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
   * https://doc.akka.io/docs/akka/current/stream/futures-interop.html#overview
   * @param in A Source stream of DroneStateRequests
   * @return A Stream of encapsulating DroneStateResponse messages
   */
  override def getDroneState(in: Source[DroneStateRequest, NotUsed]): Source[DroneState, NotUsed] = {
    // mapAsync's Parallelism = The number of Futures that shall run in parallel
    in.mapAsync(4)(request =>
      shardRegion
        .ask[Drone.DroneState](replyTo => Drone.GetDroneState(request.id, replyTo))
        .map(droneState => DroneState(health = droneState.health, battery = droneState.battery))
    )
  }

  /**
   * Implementation of the DroneState GRPC Service in protobuf file Drones.proto
   * https://doc.akka.io/docs/akka/current/stream/futures-interop.html#overview
   * @param in A Source stream of DroneLocationRequests
   * @return A Stream of encapsulated DroneStateResponse messages
   */
  override def getDroneLocation(in: Source[DroneLocationRequest, NotUsed]): Source[DroneLocation, NotUsed] = {
    // mapAsync's Parallelism = The number of Futures that shall run in parallel
    in.mapAsync(4)(request =>
      shardRegion
        .ask[Drone.DroneLocation](replyTo => Drone.GetDroneLocation(request.id, replyTo))
        .map(droneLocation => DroneLocation(lat = droneLocation.lat, lon = droneLocation.lon))
    )
  }
}
