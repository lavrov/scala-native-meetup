//> using target.platform jvm

import cats.effect.IO
import scodec.bits.ByteVector
import java.security.MessageDigest

class Digest private(md: MessageDigest) {
  def update(bytes: ByteVector): IO[Unit] = IO {
    md.update(bytes.toArray)
  }

  def digest: IO[ByteVector] = IO {
    ByteVector.view(md.digest())
  }
}

object Digest {
  def apply(): IO[Digest] = IO {
    new Digest(MessageDigest.getInstance("SHA-1"))
  }
}


