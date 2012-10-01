import sbt._
import Keys._
import PlayProject._
import scala.Some
import com.typesafe.sbtscalariform.ScalariformPlugin._
import scalariform.formatter.preferences._

object ApplicationBuild extends Build {

	val appOrganization	= "jp.furyu"
  val appName         = "play-velocity-plugin"
  val appVersion      = "1.0-SNAPSHOT"

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
    publishTo := Some(
      "RepositoryUrl" at "TODO"
    )
  ).settings(scalariformSettings: _*).settings(scalaRiformSettings)

  lazy val scalaSample = PlayProject("scala-sample", path = file("samples/scala"), mainLang = SCALA)
    .settings(scalariformSettings: _*).settings(scalaRiformSettings).dependsOn(plugin)
}
