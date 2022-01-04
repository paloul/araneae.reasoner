package paloul.araneae.cluster.rest.util

import org.slf4j.Logger
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives.{complete, extractUri}
import paloul.araneae.cluster.rest.models.Error._

/**
 * Defines Exception and Rejection handlers to be used in API definitions.
 * To use these, define implicits that call each handler constructor.
 */
object ErrorHandlers extends ErrorDataModelSupport {

  // Define a custom exception handler so we can control the format of errors
  def exceptionHandler(log: Logger): ExceptionHandler = ExceptionHandler {
    // catch all exceptions
    case e: Exception =>
      extractUri { uri =>
        log.warn(s"Request to $uri failed: ${e.getMessage}")
        complete(400, Error(400, "error", e.getMessage))
      }
  }

  // Define a custom rejection handler so we can control the format of errors.
  // Certain exceptions (like spray's DeserializationException) are treated as
  // rejections.
  def rejectionHandler(log: Logger): RejectionHandler = RejectionHandler.newBuilder()
    // parameter rejections
    .handle {
      case MalformedRequestContentRejection(message, _) =>
        extractUri { uri =>
          log.warn(s"Malformed request for $uri -- msg: '$message'")
          complete(400, Error(400, "error", s"Bad input parameter. $message"))
        }
      case RequestEntityExpectedRejection =>
        extractUri { uri =>
          log.warn(s"Request body expected but not provided for $uri")
          complete(400, Error(400, "error", "Missing required request body"))
        }
      case MalformedQueryParamRejection(parameterName, errorMsg, _) =>
        extractUri { uri =>
          log.warn(s"Malformed query param for $uri -- param: '$parameterName', msg: '$errorMsg'")
          complete(400, Error(400, "error", s"Bad query parameter '$parameterName'"))
        }
      case MissingQueryParamRejection(parameterName) =>
        extractUri { uri =>
          log.warn(s"Query param '$parameterName' expected but not provided for $uri")
          complete(400, Error(400, "error", s"Missing query param '$parameterName'"))
        }
      case ValidationRejection(msg, _) =>
        extractUri { uri =>
          log.warn(s"Validation error for $uri: '$msg'")
          complete(400, Error(400, "error", s"Invalid: $msg"))
        }
    }
    // header rejections
    .handle {
      case MissingHeaderRejection(headerName) =>
        extractUri { uri =>
          log.warn(s"Header $headerName missing for $uri")
          complete(412, Error(412, "error", s"Missing header '$headerName''"))
        }
      case MalformedHeaderRejection(headerName, errorMsg, _) =>
        extractUri { uri =>
          log.warn(s"Malformed header $headerName for $uri: $errorMsg")
          complete(412, Error(412, "error", s"Malformed header '$headerName''"))
        }
    }
    // content-type rejections
    .handle {
      case UnsupportedRequestContentTypeRejection(_) =>
        extractUri { uri =>
          log.warn(s"Content t+ype unsupported for $uri")
          complete(415, Error(415, "error", "Unsupported content type"))
        }
    }
    // other rejections
    .handleNotFound {
      extractUri { uri =>
        log.warn(s"Resource $uri not found")
        complete(404, Error(404, "not found", "Resource not found"))
      }
    }
    .result()

}
