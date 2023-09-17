val grpc = project
  .in(file("."))
  .enablePlugins(/*ScalaNativePlugin, */Http4sGrpcPlugin)
  .settings(
    scalaVersion := "3.3.0",
    Compile / PB.targets ++= Seq(
      // set grpc = false because http4s-grpc generates its own code
      scalapb.gen(grpc = false) -> (Compile / sourceManaged).value / "scalapb"
    ),
    libraryDependencies ++= Seq(
      "co.fs2" %%% "fs2-io" % "3.10-4b5f50b"
    ),
    nativeMode := "release-fast",
  )