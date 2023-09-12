//> using platform jvm
//> using native-version 0.4.15
//> using native-gc immix
//> using dep org.typelevel::cats-effect::3.5.1
//> using dep org.typelevel::cats-effect-cps::0.4.0
//> using dep co.fs2::fs2-io::3.8-1af22dd
//> using dep org.http4s::http4s-core::1.0.0-M40
//> using dep org.http4s::http4s-ember-server::1.0.0-M40
//> using dep org.http4s::http4s-dsl::1.0.0-M40
//> using dep org.scodec::scodec-bits::1.1.37
//> using dep com.github.lolgab::scala-native-crypto::0.0.4
//> using dep org.typelevel::log4cats-noop::2.6.0

import cats.syntax.all.*
import cats.effect.*
import cats.effect.cps.*
import org.http4s.ember.server.EmberServerBuilder
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.noop.NoOpFactory
import org.http4s.*
import org.http4s.dsl.request.*
import scodec.bits.ByteVector
import java.security.MessageDigest

object server extends IOApp.Simple {

  implicit val logging: LoggerFactory[IO] = NoOpFactory[IO]

  def run = createServer.useForever

  def createServer: Resource[IO, Unit] =
    EmberServerBuilder.default[IO]
      .withHttp2
      .withHttpApp(app.orNotFound)
      .build
      .void

  def app = HttpRoutes.of[IO] {
    case req @ POST -> Root / "sha1" =>
      async[IO] {
        try
          val body = req.as[String].await
          val bytes = ByteVector.fromBase64Descriptive(body) match
            case Right(bytes) => bytes
            case Left(message) => throw BadRequest(message)
          val hasher = Hasher().await
          hasher.update(bytes).await
          val digestBytes = hasher.digest.await
          Response[IO](Status.Ok).withEntity(digestBytes.toHex)
        catch
          case BadRequest(message) =>
            Response[IO](Status.BadRequest).withEntity(message)
      }
  }

  case class BadRequest(message: String) extends Throwable
}

trait Hasher {
  def update(bytes: ByteVector): IO[Unit]
  def digest: IO[ByteVector]
}

object Hasher {
  def apply(): IO[Hasher] = async[IO] {
    val md: MessageDigest = IO(MessageDigest.getInstance("SHA-1")).await
    new {
      def update(bytes: ByteVector): IO[Unit] = IO(md.update(bytes.toArray))
      def digest: IO[ByteVector] = IO(ByteVector(md.digest()))
    }
  }
}
