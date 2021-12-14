package paloul.araneae.reasoner.rest

import akka.Done
import akka.actor.CoordinatedShutdown
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, RejectionHandler, Route}
import org.slf4j.LoggerFactory
import paloul.araneae.reasoner.Settings
import paloul.araneae.reasoner.rest.util.ErrorHandlers

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

// This trait is merely support to setup the Rest Services
// Instantiates and uses the RestService class underneath
trait RestServicesSupport {

  private val log = LoggerFactory.getLogger("RestServicesSupport")

  // Define custom exception and rejection handlers so we can control the format of errors
  implicit def exceptionHandler: ExceptionHandler = ErrorHandlers.exceptionHandler(log)
  implicit def rejectionHandler: RejectionHandler = ErrorHandlers.rejectionHandler(log)

  def startRestService(externalRoutes: Route, settings: Settings) (implicit system: ActorSystem[_]): Unit = {

    val shutdown = CoordinatedShutdown(system)

    // Create each Rest Service class and get its routes
    // Each RestService class defines the routes and how to deal with each request, i.e. forward to agents
    val coreApiRoutes = new CoreApiRoutes(settings).routes
    /* NOTE: INSTANTIATE ADDITIONAL REST API ROUTES ABOVE AFTER CREATION OF NEW AGENT REST SERVICES CLASS */

    // Combine all the routes from underlying agent rest services in to one
    val combinedRoutes: Route = Route.seal(coreApiRoutes ~ externalRoutes)
    /* NOTE: APPEND ANY NEW ROUTES ABOVE TO ROUTES WITH THE ~ SYMBOL */

    val host = settings.application.akkaHttpHost // Host address to bind to
    val port = settings.application.akkaHttpPort // Port address to bind to

    // Bind to the server details given by host, port, and routes
    // If failure then terminate the whole system
    Http().newServerAt(host, port).bind(combinedRoutes).onComplete {
      case Success(serverBinding) =>
        val address = serverBinding.localAddress
        log.info("Rest API bound to http://{}:{}/", address.getHostString, address.getPort)

        // Setup a graceful terminate
        shutdown.addTask(CoordinatedShutdown.PhaseServiceRequestsDone, "http-graceful-terminate") { () =>
          serverBinding.terminate(5.seconds).map { _ =>
            log.info("{} http://{}:{}/ graceful shutdown completed",
              settings.application.akkaClusterName, address.getHostString, address.getPort)
            Done
          }(ExecutionContext.global)
        }

      case Failure(ex) =>
        log.error("Failed to bind to HTTP Endpoint, terminating system", ex)
        system.terminate()

    }(ExecutionContext.global)
  }
}
