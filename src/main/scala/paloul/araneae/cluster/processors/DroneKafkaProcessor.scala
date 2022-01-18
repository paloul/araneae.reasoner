package paloul.araneae.cluster.processors

import akka.actor.typed.{ActorRef, Behavior}
import paloul.araneae.cluster.actors.Drone
import paloul.araneae.cluster.util.Settings

object DroneKafkaProcessor {

  //************************************************************************************
  // Drone Messages

  /** Base Message type messages */
  sealed trait Command
  //************************************************************************************

  def apply(shardRegion: ActorRef[Drone.Command], settings: Settings): Behavior[Nothing] = {

  }

  // TODO: Implement me

}
