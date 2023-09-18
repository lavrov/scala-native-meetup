import cats.effect.*
import cats.effect.cps.*
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.noop.NoOpFactory
import org.http4s.*
import org.http4s.syntax.all.*
import scodec.bits.ByteVector
import fs2.io.file.Files
import fs2.io.file.Flags
import fs2.io.file.Path

object client extends IOApp {

  implicit val logging: LoggerFactory[IO] = NoOpFactory[IO]

  def run(args: List[String]) =
    createClient.use { client =>
      async[IO] {
        Files[IO]
          .readAll(Path(args.mkString), 1024 * 64, Flags.Read)
          .chunks
          .evalTap { chunk =>
            val bytes = chunk.toByteVector
            val request = Request[IO](
              method = Method.POST,
              uri = uri"http://localhost:8080/sha1"
            )
            .withEntity(bytes.toBase64)
            client.expect[String](request)
          }
          .compile
          .drain
          .await
        ExitCode.Success
      }
    }

  def createClient: Resource[IO, Client[IO]] = {
    EmberClientBuilder
      .default[IO]
      .withHttp2
      .build
  }
}
