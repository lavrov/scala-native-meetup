//> using target.platform native

import cats.effect.IO
import scodec.bits.ByteVector

import scala.scalanative.unsafe.*
import scala.scalanative.unsigned.*

@link("crypto")
@extern
private object crypto {
  def SHA1_Init(c: Ptr[Byte]): CInt = extern
  def SHA1_Update(c: Ptr[Byte], data: Ptr[Byte], len: CSize): CInt = extern
  def SHA1_Final(md: CString, c: Ptr[Byte]): CInt = extern
}

class Digest private(ctx: Ptr[Byte]) {
  def update(bytes: ByteVector): IO[Unit] = IO {
    crypto.SHA1_Update(
      ctx,
      bytes.toArray.atUnsafe(0),
      bytes.size.toULong
    )
  }

  def digest: IO[ByteVector] = IO {
    val md = new Array[Byte](20)
    crypto.SHA1_Final(md.atUnsafe(0), ctx)
    ByteVector.view(md)
  }
}

object Digest {
  def apply(): IO[Digest] = IO {
    val ctx = new Array[Byte](450).atUnsafe(0)
    crypto.SHA1_Init(ctx)
    new Digest(ctx)
  }
}


