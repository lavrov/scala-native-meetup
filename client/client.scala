//> using jvm 17
//> using native-version 0.4.15
//> using dep org.typelevel::cats-effect::3.5.1
//> using dep org.typelevel::cats-effect-cps::0.4.0
//> using dep co.fs2::fs2-io::3.8-1af22dd
//> using dep org.http4s::http4s-core::1.0.0-M40
//> using dep org.http4s::http4s-ember-client::1.0.0-M40
//> using dep org.http4s::http4s-dsl::1.0.0-M40
//> using dep org.scodec::scodec-bits::1.1.37
//> using dep org.typelevel::log4cats-noop::2.6.0

import cats.syntax.all.*
import cats.effect.*
import cats.effect.cps.*
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.noop.NoOpFactory
import org.http4s.*
import org.http4s.dsl.request.*
import org.http4s.syntax.all.*
import scodec.bits.ByteVector
import fs2.Stream
import fs2.Pipe
import fs2.io.file.Files
import fs2.io.file.Flags
import fs2.io.file.Path
import java.security.MessageDigest

object client extends IOApp {

  implicit val logging: LoggerFactory[IO] = NoOpFactory[IO]

  def run(args: List[String]) =
    createClient.use { client =>
      async[IO] {
        val inputString = args.mkString
        val bytes = ByteVector.encodeUtf8(inputString).liftTo[IO].await
        val request = Request[IO](
          method = Method.POST,
          uri = uri"http://localhost:8080/sha1"
        )
          .withEntity(bytes.toBase64)
        client.expect[String](request).await
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
