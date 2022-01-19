package paloul.araneae.cluster.processors

import akka.Done
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import paloul.araneae.cluster.actors.Drone
import paloul.araneae.cluster.util.Settings

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Try

object DroneKafkaProcessor {

  //************************************************************************************
  // DroneKafkaProcessor Messages

  /** Base Message type messages */
  sealed trait Command

  // Internal notification message to self to signal consumer stopped
  private case class KafkaConsumerStopped(reason: Try[Any]) extends Command
  //************************************************************************************

  def apply(shardRegion: ActorRef[Drone.Command], settings: Settings): Behavior[Nothing] = {
    Behaviors
      .setup[Command] { context =>
        val result = startConsumingFromTopic(context.system, shardRegion, settings)

        context.pipeToSelf(result) {
          result => KafkaConsumerStopped(result)
        }

        Behaviors.receiveMessage[Command] {
          case KafkaConsumerStopped(reason) =>
            context.log.info("Consumer stopped {}", reason)
            Behaviors.stopped
        }

      }.narrow
  }

  private def startConsumingFromTopic(actorSystem: ActorSystem[_],
                                      shardRegion: ActorRef[Drone.Command],
                                      settings: Settings): Future[Done] = {

    // TODO: Implement me

  }

}
