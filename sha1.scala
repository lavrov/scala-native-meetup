//> using jvm 17
//> using dep org.typelevel::cats-effect::3.5.1
//> using dep org.typelevel::cats-effect-cps::0.4.0
//> using dep co.fs2::fs2-io::3.9.1
//> using dep org.scodec::scodec-bits::1.1.37

import cats.effect.*
import cats.effect.cps.*
import fs2.Stream
import fs2.Pipe
import fs2.io.file.Files
import fs2.io.file.Flags
import fs2.io.file.Path
import java.security.MessageDigest
import scodec.bits.ByteVector

object files extends IOApp {

  def run(args: List[String]): IO[ExitCode] = async[IO]:
    val md = MessageDigest.getInstance("SHA-1")
    val filePath = args match
      case List(fileName) => Path(fileName)
      case _ => throw new IllegalArgumentException("Usage: sha1 <file>")
    Files[IO]
      .readAll(filePath, 1024 * 1024, Flags.Read)
      .through(sha1Pipe)
      .compile
      .lastOrError
      .flatMap(digest => IO.println(digest.toHex))
      .await
    ExitCode.Success

  def sha1Pipe: Pipe[IO, Byte, ByteVector] = bytes =>
    Stream
      .eval(IO(MessageDigest.getInstance("SHA-1")))
      .evalTap(md =>
        bytes
          .chunks
          .evalMap(bytes => IO(md.update(bytes.toArray)))
          .compile
          .drain
      )
      .evalMap(md => IO(ByteVector(md.digest())))

}

@main def stats: Unit =
  println("Hello, world!")