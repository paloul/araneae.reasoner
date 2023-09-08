package paloul.araneae.cluster.services.grpc

import akka.NotUsed
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.{ActorRef, ActorSystem, Scheduler}
import akka.stream.scaladsl.{Sink, Source}
import akka.util.Timeout
import paloul.araneae.cluster.actors.Agent
import paloul.araneae.cluster.util.Settings
import paloul.araneae.cluster.protobuf.{AgentLocation, AgentLocationRequest, AgentService, AgentState, AgentStateRequest}

import scala.concurrent.{ExecutionContextExecutor, Future}

/**
 * Implementation of the GRPC Agent Service trait defined in Protobuf file Agent.proto
 * @param system Typed Actor System reference
 * @param shardRegion Agent Shard Region Manager reference
 * @param settings Reference to Settings for access to configuration env variables
 */
class AgentGrpcService(system: ActorSystem[_], shardRegion: ActorRef[Agent.Command], settings: Settings) extends AgentService {

  implicit val scheduler: Scheduler = system.scheduler
  implicit val ec: ExecutionContextExecutor = system.executionContext
  implicit val timeout: Timeout = Timeout(settings.application.akkaAskTimeout.length, settings.application.akkaAskTimeout.unit)

  /**
   * Implementation of the AgentState GRPC Service in protobuf file Agents.proto
   * https://doc.akka.io/docs/akka/current/stream/futures-interop.html#overview
   * @param in A Source stream of AgentStateRequests
   * @return A Stream of encapsulating AgentStateResponse messages
   */
  override def getAgentState(in: Source[AgentStateRequest, NotUsed]): Source[AgentState, NotUsed] = {
    // mapAsync's Parallelism = The number of Futures that shall run in parallel
    in.mapAsync(4)(request =>
      shardRegion
        .ask[Agent.AgentState](replyTo => Agent.GetAgentState(request.id, replyTo))
        .map(agentState => AgentState(health = agentState.health, battery = agentState.battery))
    )
  }

  /**
   * Implementation of the AgentState GRPC Service in protobuf file Agents.proto
   * https://doc.akka.io/docs/akka/current/stream/futures-interop.html#overview
   * @param in A Source stream of AgentLocationRequests
   * @return A Stream of encapsulated AgentStateResponse messages
   */
  override def getAgentLocation(in: Source[AgentLocationRequest, NotUsed]): Source[AgentLocation, NotUsed] = {
    // mapAsync's Parallelism = The number of Futures that shall run in parallel
    in.mapAsync(4)(request =>
      shardRegion
        .ask[Agent.AgentLocation](replyTo => Agent.GetAgentLocation(request.id, replyTo))
        .map(agentLocation => AgentLocation(lat = agentLocation.lat, lon = agentLocation.lon))
    )
  }
}
