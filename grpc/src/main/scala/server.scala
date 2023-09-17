import amsterdam_scala.HelloWorld.{HelloRequest, HelloResponse, HelloWorld}
import cats.effect.*
import org.http4s.Headers
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server

object server extends IOApp.Simple {

  // server with ember
  def run: IO[Unit] =
    createServer.useForever

  def createServer: Resource[IO, Server] =
    EmberServerBuilder.default[IO]
      .withHttp2
      .withHttpApp(HelloWorld.toRoutes(helloWorldImpl).orNotFound)
      .build

  def helloWorldImpl = new HelloWorld[IO] {
    def sayHello(request: HelloRequest, ctx: Headers): IO[HelloResponse] =
      IO.pure(HelloResponse(s"Hello, ${request.name}!"))
  }
}