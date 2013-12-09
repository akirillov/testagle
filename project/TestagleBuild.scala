import sbt._
import Keys._
import scalabuff.ScalaBuffPlugin._

object TestagleBuild extends Build {

	val finaglecore = "com.twitter" %% "finagle-core" % "6.8.1"
  val finaglehttp = "com.twitter" %% "finagle-http" % "6.8.1"
  val scalabuff = "net.sandrogrzicic" %% "scalabuff-runtime" % "1.3.6"

  val specs2 = "org.specs2" %% "specs2" % "2.3.4" % "test"

  lazy val root = Project(id = "testagle", base = file(".")) aggregate (core, api, example) dependsOn (core, api, example)

  lazy val core = Project(id = "testagle-core", base = file("testagle-core"), settings = Defaults.defaultSettings ++ scalabuffSettings)
  .configs(ScalaBuff).settings(libraryDependencies ++= Seq(finaglecore, specs2), exportJars := true)
  .dependsOn(api, uri("git://github.com/sbt/sbt-assembly.git#3e6dfa2"))

  lazy val api = Project(id = "testagle-api", base = file("testagle-api")) settings (exportJars := true)

  lazy val example = Project(id = "testagle-example", base = file("testagle-example")) settings (libraryDependencies ++= Seq(finaglecore, finaglehttp), exportJars := true) dependsOn api
}

