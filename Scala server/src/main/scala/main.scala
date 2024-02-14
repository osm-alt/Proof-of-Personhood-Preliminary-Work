import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.io.StdIn
import akka.stream.scaladsl.Flow
import akka.http.scaladsl.model.ws.{Message, TextMessage}

import scala.concurrent.ExecutionContextExecutor

object main extends App {

  // implicit values required by the server machinery
  implicit val actorSystem: ActorSystem = akka.actor.ActorSystem("messaging-actorsystem")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = actorSystem.dispatcher

  // define a basic route ("/") that returns a welcoming message
  def helloRoute: Route = path("hello") {
    complete("Scala Websocket server")
  }

  // create WebSocket route
  def countRoute = pathEndOrSingleSlash {
    var count = 0
    handleWebSocketMessages(
      Flow[Message].collect {
        case TextMessage.Strict(text) => {
          println("Received: " + text)
          val received = text.toInt
          count += received
          println("Sending: " + count)
          TextMessage(count.toString)
        }
      }
    )
  }

  // bind the route using HTTP to the server address and port
  val binding = Http().newServerAt("localhost", 11337).bind(helloRoute ~ countRoute)
  println("Server running...")

  // kill the server with input
  StdIn.readLine()
  binding.flatMap(_.unbind()).onComplete(_ => actorSystem.terminate())
  println("Server is shut down")
}