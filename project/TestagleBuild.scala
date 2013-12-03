import sbt._
import Keys._

object HelloBuild extends Build {

	lazy val root = project.in(file(".")).aggregate(core, api)

	lazy val core = project.in(file("testagle-core")).dependsOn(api)
	lazy val api = project.in(file("testagle-api"))
}
