import sbt._
import Keys._
import scalabuff.ScalaBuffPlugin._

object TestagleBuild extends Build {

	val finaglecore = "com.twitter" %% "finagle-core" % "6.8.1"
  val scalabuf = "net.sandrogrzicic" %% "scalabuff-runtime" % "1.3.6"

  lazy val root = Project(id = "testagle", base = file("."), settings = Defaults.defaultSettings ++ scalabuffSettings) configs(ScalaBuff) aggregate (core, api)

  lazy val core = Project(id = "testagle-core", base = file("testagle-core")) settings(libraryDependencies ++= Seq(finaglecore, scalabuf), exportJars := true) dependsOn api

  lazy val api = Project(id = "testagle-api", base = file("testagle-api")) settings (exportJars := true)

}

