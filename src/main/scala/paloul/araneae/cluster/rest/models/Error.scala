package paloul.araneae.cluster.rest.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object Error {
  // Error Response Model
  final case class Error(code: Int, status: String, message: String)

  trait ErrorDataModelSupport extends SprayJsonSupport with DefaultJsonProtocol {
    // Error
    implicit val errorFormat: RootJsonFormat[Error] = jsonFormat3(Error)
  }
}
