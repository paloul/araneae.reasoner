package paloul.araneae.reasoner.rest

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives._
import paloul.araneae.reasoner.Settings
import paloul.araneae.reasoner.util.LoggerEnabled

/**
 * CoreApiRoutes is the general Route class. This is where certain things
 * like health checks go into. Any other APIs that are necessary to expose
 * for the cluster as a whole, maintenance, get stats, etc.
 *
 * @param settings The settings from app.conf as a class
 */
class CoreApiRoutes(settings: Settings) (implicit system: ActorSystem[_]) extends LoggerEnabled {

  // All route subroutines below should be added to this definition.
  // This routes definition is publicly available to the outside
  def routes: server.Route = isAlive ~ getVersion

  //------------------------------------------------------------------------//
  // Begin API Routes
  // The routes below utilize the implicit timeout carried over from class instantiation
  //------------------------------------------------------------------------//

  /**
   * API GET handler for generic is alive check /api/is_alive
   * @return
   */
  private def isAlive = {
    get {
      pathPrefix("core" / "api" / "is_alive") {
        pathEndOrSingleSlash {
          log.debug("GET [is_alive]")

          complete(OK)
        }
      }
    }
  }

  /**
   * API GET handler for checking deployed version /api/version
   * @return
   */
  private def getVersion = {
    get {
      pathPrefix("core" / "api" / "version") {
        pathEndOrSingleSlash {
          log.debug("GET [version]")

          complete(OK)
        }
      }
    }
  }
  //------------------------------------------------------------------------//
  // End API Routes
  //------------------------------------------------------------------------//

}
