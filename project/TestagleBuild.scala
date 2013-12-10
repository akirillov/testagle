import sbt._
import Keys._
import scalabuff.ScalaBuffPlugin._
import xerial.sbt.Pack._

object TestagleBuild extends Build {

	val finaglecore = "com.twitter" %% "finagle-core" % "6.8.1"
  val finaglehttp = "com.twitter" %% "finagle-http" % "6.8.1"
  val scalabuff = "net.sandrogrzicic" %% "scalabuff-runtime" % "1.3.6"

  val specs2 = "org.specs2" %% "specs2" % "2.3.4" % "test"

  lazy val root = Project(id = "testagle", base = file(".")) aggregate (core, api, example) dependsOn (core, api, example)

  lazy val core = Project(
    id = "testagle-core",
    base = file("testagle-core"),
    settings = Defaults.defaultSettings
      ++ scalabuffSettings
      ++ packSettings
      ++ Seq(
      packMain := Map("startNode" -> "io.testagle.core.server.TestagleServer"),
      libraryDependencies ++= Seq(finaglecore, specs2),
      exportJars := true
    )
  ).configs(ScalaBuff).dependsOn(api)

  lazy val api = Project(id = "testagle-api", base = file("testagle-api")) settings (exportJars := true)

  lazy val example = Project(id = "testagle-example", base = file("testagle-example")) settings (libraryDependencies ++= Seq(finaglecore, finaglehttp), exportJars := true) dependsOn api
}

