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
package jp.furyu.play.velocity.mvc

import org.specs2.mutable._
import play.api.test.Helpers._
import play.api.test.FakeApplication

class VMSpec extends Specification {

  private def runApp[T](block: => T): T = {
    running(FakeApplication(
      additionalPlugins = Seq("jp.furyu.play.velocity.VelocityPlugin"),
      additionalConfiguration = Map("ehcacheplugin" -> "disabled") // without this, error [Cache play already exists] occurred.
    )) {
      block
    }
  }

  "VM" should {

    "template file not found" in runApp {
      VM("no_template.vm") must throwA[org.apache.velocity.exception.ResourceNotFoundException]
    }

    "template file found" in runApp {
      VM("test_template.vm").body === """${fuga}"""
    }

    "evaluate variables" in runApp {
      VM("test_template.vm", Map("fuga" -> "fuga_value")).body === """fuga_value"""
    }

    "evaluate scala iterable" in runApp {
      VM("test_template_for_scala_iterable.vm", Map("list" -> List(1, 2, 3))).body === """  1
|  2
|  3
""".stripMargin
    }

    "evaluate scala field access" in runApp {
      case class Fuga(piyo: String)
      VM("test_template_for_scala_field.vm", Map("fuga" -> Fuga("__piyo__"))).body === """__piyo__"""
    }

  }

}
