import amsterdam_scala.HelloWorld.{HelloRequest, HelloResponse, HelloWorld}
import cats.effect.*
import org.http4s.Headers
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.implicits.uri

object client extends IOApp.Simple {

  def run: IO[Unit] =
    createServer.use(helloWorld =>
      helloWorld.sayHello(HelloRequest("Bob"), Headers.empty).flatMap(resp =>
        IO.println(s"Response: ${resp.message}")
      )
    )

  def createServer: Resource[IO, HelloWorld[IO]] =
    EmberClientBuilder.default[IO]
      .withHttp2
      .build
      .map(HelloWorld.fromClient(_, uri"http://localhost:8080"))

  def helloWorldImpl = new HelloWorld[IO] {
    def sayHello(request: HelloRequest, ctx: Headers): IO[HelloResponse] =
      IO.pure(HelloResponse(s"Hello, ${request.name}!"))
  }
}