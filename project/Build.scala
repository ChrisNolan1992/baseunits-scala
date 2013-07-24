import sbt._
import Keys._

object BaseUnitsBuild extends Build {

  lazy val root = Project(id = "baseunits-scala",
    base = file("."),
    settings = Defaults.defaultSettings ++ Seq(
      organization := "org.sisioh",
      version := "0.1.6",
      scalaVersion := "2.10.2",
      scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation"),
      resolvers ++= Seq(
        "Twitter Repository" at "http://maven.twttr.com/",
        "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
        "Sisioh Maven Relase Repository" at "http://sisioh.github.com/scala-dddbase/repos/release/",
        "Sisioh Maven Snapshot Repository" at "http://sisioh.github.com/scala-dddbase/repos/snapshot/"
      ),
      libraryDependencies ++= Seq(
        "junit" % "junit" % "4.8.1" % "test",
        "org.mockito" % "mockito-core" % "1.9.5" % "test",
        "org.scalatest" %% "scalatest" % "1.9.1" % "test",
        "commons-io" % "commons-io" % "2.4",
        "org.sisioh" % "scala-dddbase-spec_2.10" % "0.1.20",
        "org.sisioh" %% "scala-toolbox" % "0.0.6"
      ),
      publishMavenStyle := true,
      publishTo <<= (version) { version: String =>
        if (version.trim.endsWith("SNAPSHOT")) {
          Some(Resolver.file("snaphost", new File("./repos/snapshot")))
        }else{
          Some(Resolver.file("release", new File("./repos/release")))
        }
      }
    )
  )

}
