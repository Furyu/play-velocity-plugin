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
package jp.furyu.play.velocity

import org.specs2.mutable._
import play.api.test.Helpers._
import play.api.test.FakeApplication

class VelocityPluginSpec extends Specification {

  private def ApplicationWithVelocityPlugin = FakeApplication(
    additionalPlugins = Seq("jp.furyu.play.velocity.VelocityPlugin"),
    additionalConfiguration = Map("ehcacheplugin" -> "disabled") // without this, error [Cache play already exists] occurred.
  )

  private def runApp[T](app: FakeApplication)(block: FakeApplication => T): T = {
    running(app) {
      block(app)
    }
  }

  "VelocityPlugin" should {

    "not installed VelocityPlugin" in runApp(FakeApplication()) { app =>
      app.plugin[VelocityPlugin] must beNone
    }

    "installed VelocityPlugin" in runApp(ApplicationWithVelocityPlugin) { app =>
      app.plugin[VelocityPlugin] must beSome[jp.furyu.play.velocity.VelocityPlugin]
      app.plugin[VelocityPlugin].get.enabled must beTrue
    }

    "load extended properties" in runApp(ApplicationWithVelocityPlugin) { app =>
      app.plugin[VelocityPlugin].get.engine.getProperty("hoge") === "fuga"
    }

  }
}
