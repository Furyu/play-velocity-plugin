/*
 * Copyright (C) 2013 FURYU CORPORATION
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Includes Apache Velocity
 *
 *   http://velocity.apache.org/
 *
 * Copyright (C) 2000-2007 The Apache Software Foundation
 */
import sbt._
import Keys._
import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform._

object ApplicationBuild extends Build {

  val appOrganization	= "jp.furyu"
  val appName         = "play-velocity-plugin"
  val appScalaVersion = "2.10.0"
  val appScalaCrossVersions = Seq(appScalaVersion, "2.9.1")
  // version is defined in version.sbt in order to support sbt-release

  lazy val appScalaRiformSettings = ScalariformKeys.preferences := FormattingPreferences().setPreference(IndentWithTabs, false).setPreference(DoubleIndentClassDeclaration, true).setPreference(PreserveDanglingCloseParenthesis, true)

  lazy val root = Project("root", base = file("."))
    .dependsOn(plugin)
    .aggregate(scalaSample)

  lazy val plugin = Project(appName, base = file("plugin")).settings(Defaults.defaultSettings: _*)
    .settings(appPublishSettings: _*)
    .settings(appReleaseSettings: _*)
    .settings(scalariformSettings: _*)
    .settings(appScalaRiformSettings)
    .settings(
      scalaVersion := appScalaVersion,
      crossScalaVersions := appScalaCrossVersions,
      resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
      libraryDependencies <+= scalaVersion(v => {
        v match {
          case "2.9.1" | "2.9.2" => "play" %% "play" % "[2.0,)" % "provided"
          case _ => "play" % "play" % "[2.0,)" % "provided" cross CrossVersion.binaryMapped {
            case "2.10.0" => "2.10"
            case x => x
          }
        }
      }),
      libraryDependencies <+= scalaVersion(v => {
        v match {
          case "2.10.0" => "org.specs2" %% "specs2" % "1.14" % "test"
          case "2.9.1" => "org.specs2" %% "specs2" % "1.12.3" % "test"
        }
      }),
      libraryDependencies ++= Seq(
        "org.apache.velocity" % "velocity" % "[1.7,)",
        "play" %% "play-test" % "[2.0,)" % "test",
        "commons-lang" % "commons-lang" % "2.6"
      )
    )

  lazy val scalaSample = play.Project("scala-sample", path = file("samples/scala")).settings( 
    scalaVersion := appScalaVersion
  ).settings(com.typesafe.sbt.SbtScalariform.scalariformSettings: _*).settings(appScalaRiformSettings).dependsOn(plugin)

  lazy val appPublishSettings = Seq(
    // version is defined in version.sbt in order to support sbt-release
    organization := appOrganization,
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
  )

  lazy val appReleaseSettings = {
    // relese for cross build : ]# sbt release cross
    sbtrelease.ReleasePlugin.releaseSettings ++ Seq(
      sbtrelease.ReleasePlugin.ReleaseKeys.versionFile := file("project/version.sbt"),
      sbtrelease.ReleasePlugin.ReleaseKeys.releaseProcess := Seq[sbtrelease.ReleaseStep](
        sbtrelease.ReleaseStateTransformations.checkSnapshotDependencies,
        sbtrelease.ReleaseStateTransformations.inquireVersions,
        sbtrelease.ReleaseStateTransformations.runTest,
        sbtrelease.ReleaseStateTransformations.setReleaseVersion,
        sbtrelease.ReleaseStateTransformations.commitReleaseVersion,
        sbtrelease.ReleaseStateTransformations.publishArtifacts,
        sbtrelease.ReleaseStateTransformations.setNextVersion,
        sbtrelease.ReleaseStateTransformations.commitNextVersion
      )
    )
  }

}
