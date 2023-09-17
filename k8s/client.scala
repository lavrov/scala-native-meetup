//> using platform native
//> using native-version 0.4.15
//> using dep org.typelevel::cats-effect::3.5.1
//> using dep org.typelevel::cats-effect-cps::0.4.0
//> using dep co.fs2::fs2-io::3.10-4b5f50b
//> using dep org.http4s::http4s-core::0.23.23
//> using dep org.http4s::http4s-ember-server::0.23.23
//> using dep org.http4s::http4s-ember-client::0.23.23
//> using dep org.http4s::http4s-dsl::0.23.23
//> using dep org.http4s::http4s-circe::0.23.23
//> using dep org.scodec::scodec-bits::1.1.37
//> using dep org.typelevel::log4cats-noop::2.6.0
//> using dep dev.hnaderi::scala-k8s-http4s-ember::0.15.0
//> using dep dev.hnaderi::scala-k8s-circe::0.15.0

import cats.effect.*
import cats.effect.cps.*
import dev.hnaderi.k8s.circe.*
import dev.hnaderi.k8s.client.*
import dev.hnaderi.k8s.client.http4s.EmberKubernetesClient
import io.circe.Json
import org.http4s.circe._

object client extends IOApp.Simple {

  def run: IO[Unit] =
    buildClient.use(client =>
      async[IO] {
        val cm =
          APIs
            .namespace("media-server")
            .configmaps
            .get("config")
            .send(client)
            .await
        IO.println(cm.toString).await
      }
    )

  val buildClient = EmberKubernetesClient[IO].defaultConfig[Json]
}