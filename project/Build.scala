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

object ApplicationBuild extends Build {

  val appOrganization	= "jp.furyu"
  val appName         = "play-velocity-plugin"
  val appVersion      = "1.3-SNAPSHOT"
  val appScalaVersion = "2.11.1"
  val appScalaCrossVersions = Seq(appScalaVersion, "2.10.4")

  lazy val plugin = Project(appName, base = file("plugin"))
    .settings(appPublishSettings: _*)
    .settings(appScalariformSettings: _*)
    .settings(
      scalaVersion := appScalaVersion,
      crossScalaVersions := appScalaCrossVersions,
      resolvers += Resolver.typesafeRepo("releases"),
      libraryDependencies ++= Seq(
        "com.typesafe.play" %% "play" % "2.3.9",
        "org.apache.velocity" % "velocity" % "[1.7,)",
        "commons-lang" % "commons-lang" % "2.6",
//        "org.specs2" %% "specs2-core" % "3.6" % "test",
        "com.typesafe.play" %% "play-test" % "2.3.9" % "test"
      )
    )

  lazy val appPublishSettings = Seq(
    // version is defined in version.sbt in order to support sbt-release
    organization := appOrganization,
    publishMavenStyle := true,
    version := appVersion,
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

  private lazy val appScalariformSettings = {
    import com.typesafe.sbt.SbtScalariform
    import scalariform.formatter.preferences._

    SbtScalariform.scalariformSettings ++ Seq(
      SbtScalariform.ScalariformKeys.preferences := FormattingPreferences()
        .setPreference(IndentWithTabs, false)
        .setPreference(DoubleIndentClassDeclaration, true)
        .setPreference(PreserveDanglingCloseParenthesis, true)
    )
  }

}
