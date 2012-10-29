package jp.furyu.play.velocity.mvc

import org.specs2.mutable._
import play.api.test.Helpers._
import play.api.test.FakeApplication

import org.apache.velocity.app.VelocityEngine
import org.specs2.execute.Failure

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

    "DefaultProperty" in {
      "return DefaultProperty instance" in {
        val app = FakeApplication(
          additionalPlugins = Seq("jp.furyu.play.velocity.VelocityPlugin"),
          additionalConfiguration = Map("ehcacheplugin" -> "disabled") // without this, error [Cache play already exists] occurred.
        )
        running(app) {
          DefaultProperty === app.plugin[jp.furyu.play.velocity.VelocityPlugin].get.DefaultProperty
        }
      }

      "evaluate use argument engine" in runApp {
        val property = DefaultProperty
        property.setProperty("runtime.references.strict", "true")
        val engine = new VelocityEngine(property)

        try {
          VM("test_template.vm", Map.empty, engine).body === """fuga_value"""
          failure("VelocityEigin is not strict mode")
        } catch {
          case e: org.apache.velocity.exception.MethodInvocationException => e.getMessage must contain("fuga")
        }
      }
    }

    "use" in {
      "set engine to use for evaluate templates" in runApp {
        val property = DefaultProperty
        property.setProperty("runtime.references.strict", "true")
        val engine = new VelocityEngine(property)

        use(engine)

        try {
          VM("test_template.vm", Map.empty, engine).body === """fuga_value"""
          failure("VelocityEigin is not strict mode")
        } catch {
          case e: org.apache.velocity.exception.MethodInvocationException => e.getMessage must contain("fuga")
        }
      }
    }

  }

}
