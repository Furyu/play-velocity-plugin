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
