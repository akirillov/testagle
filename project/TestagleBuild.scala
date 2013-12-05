import sbt._
import Keys._
import scalabuff.ScalaBuffPlugin._

object TestagleBuild extends Build {

	val finaglecore = "com.twitter" %% "finagle-core" % "6.8.1"
  val scalabuff = "net.sandrogrzicic" %% "scalabuff-runtime" % "1.3.6"

  lazy val root = Project(id = "testagle", base = file(".")) aggregate (core, api) dependsOn (core, api)

  lazy val core = Project(id = "testagle-core", base = file("testagle-core"), settings = Defaults.defaultSettings ++ scalabuffSettings) configs(ScalaBuff) settings(libraryDependencies ++= Seq(finaglecore), exportJars := true) dependsOn api

  lazy val api = Project(id = "testagle-api", base = file("testagle-api")) settings (exportJars := true)

}

