import sbt._
import Keys._
import PlayProject._
import scala.Some
import com.typesafe.sbtscalariform.ScalariformPlugin._
import scalariform.formatter.preferences._

object ApplicationBuild extends Build {

	val appOrganization	= "jp.furyu"
  val appName         = "play-velocity-plugin"
  val appVersion      = "1.1-SNAPSHOT"

  lazy val scalaRiformSettings = ScalariformKeys.preferences := FormattingPreferences().setPreference(IndentWithTabs, false).setPreference(DoubleIndentClassDeclaration, true).setPreference(PreserveDanglingCloseParenthesis, true)

  lazy val root = Project("root", base = file(".")).dependsOn(plugin).aggregate(scalaSample)

  lazy val plugin = Project(appName, base = file("plugin")).settings(
    libraryDependencies ++= Seq(
      "play" %% "play" % "2.0",
      "org.apache.velocity" % "velocity" % "1.7" withSources()
    ),
    organization := appOrganization,
    version := appVersion,
    publishMavenStyle := true,
    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT")) {
        Some("snapshots" at nexus + "content/repositories/snapshots")
      } else {
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
      }
    },
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    pomExtra := (
      <url>https://github.com/Furyu/play-velocity-plugin</url>
      <licenses>
        <license>
          <name>Apache License, Version 2.0</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:Furyu/play-velocity-plugin.git</url>
        <connection>scm:git:git@github.com:Furyu/play-velocity-plugin.git</connection>
      </scm>
      <developers>
        <developer>
          <id>flysheep1980</id>
          <name>flysheep1980</name>
          <url>https://github.com/flysheep1980</url>
        </developer>
      </developers>
    )
  ).settings(scalariformSettings: _*).settings(scalaRiformSettings)

  lazy val scalaSample = PlayProject("scala-sample", path = file("samples/scala"), mainLang = SCALA)
    .settings(scalariformSettings: _*).settings(scalaRiformSettings).dependsOn(plugin)
}
