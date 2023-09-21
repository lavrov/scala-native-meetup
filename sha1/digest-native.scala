//> using target.platform native

import cats.effect.IO
import scodec.bits.ByteVector

import scala.scalanative.unsafe.*
import scala.scalanative.unsigned.*

/**
 * https://manpages.ubuntu.com/manpages/trusty/man3/SHA1_Init.3ssl.html
 *
 * #include <openssl/sha.h>
 *
 * unsigned char *SHA1(const unsigned char *d, unsigned long n,
 * unsigned char *md);
 *
 * int SHA1_Init(SHA_CTX *c);
 * int SHA1_Update(SHA_CTX *c, const void *data, unsigned long len);
 * int SHA1_Final(unsigned char *md, SHA_CTX *c);
 *
 * SHA-1 (Secure Hash Algorithm) is a cryptographic hash function with a 160 bit output.
 *
 * SHA1() computes the SHA-1 message digest of the n bytes at d and places it in md (which
 * must have space for SHA_DIGEST_LENGTH == 20 bytes of output). If md is NULL, the digest is
 * placed in a static array.
 *
 * The following functions may be used if the message is not completely stored in memory:
 *
 * SHA1_Init() initializes a SHA_CTX structure.
 *
 * SHA1_Update() can be called repeatedly with chunks of the message to be hashed (len bytes
 * at data).
 *
 * SHA1_Final() places the message digest in md, which must have space for SHA_DIGEST_LENGTH == 20 bytes
 * of output, and erases the SHA_CTX.
 * */

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


