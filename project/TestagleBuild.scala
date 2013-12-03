import sbt._
import Keys._

object HelloBuild extends Build {



	val finaglecore = "com.twitter" %% "finagle-core" % "6.8.1"


	lazy val testagle = project.in(file(".")).aggregate(core, api)

	lazy val core = project.in(file("testagle-core")).dependsOn(api).settings(
		name := "testagle-core",
		libraryDependencies ++= Seq(finaglecore)
	)

	lazy val api = project.in(file("testagle-api"))
}

