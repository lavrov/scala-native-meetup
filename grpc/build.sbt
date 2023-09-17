val grpc = project
  .in(file("."))
  .enablePlugins(ScalaNativePlugin, Http4sGrpcPlugin)
  .settings(
    scalaVersion := "3.3.0",
    Compile / PB.targets ++= Seq(
      // set grpc = false because http4s-grpc generates its own code
      scalapb.gen(grpc = false) -> (Compile / sourceManaged).value / "scalapb"
    ),
    resolvers ++= Resolver.sonatypeOssRepos("snapshots"),
    libraryDependencies ++= Seq(
      "co.fs2" %%% "fs2-io" % "3.10-4b5f50b",
      "org.http4s" %%% "http4s-ember-server" % "0.23.23-67-0c38b06-SNAPSHOT"
    ),
    nativeMode := "release-fast",
  )