# Velocity Plugin for Play2 framework

supported play version is ```2.0.x```, ```2.1.x``` and ```2.2.x```.

In addition, also support `scala` project.

## What is Velocity?

See [Apache Velocity Site](http://velocity.apache.org/)

# Release Note

| Version | Release Date | Description |
|:----------|:----------|:------------|
| 1.1 | 2013/04/08 | enhancement #5 #12 |
| 1.1.1 | 2013/09/18 | bug fixed #13 |
| 1.2 | 2014/01/10 | supported play 2.2.x |
| 1.3 | 2015/06/17 | CrossBuild Scala2.10/2.11 |

# Usage.

## 1. Install Plugin.

Edit file `project/Build.scala` or `build.sbt`

```
libraryDependencies ++= Seq(
  "jp.furyu" %% "play-velocity-plugin" % "LATEST_VERSION"
)
```

Edit file `conf/play.plugins`

```
5000:jp.furyu.play.velocity.VelocityPlugin
```

## 2. Call `VM` Function in your Controller.

```
import jp.furyu.play.velocity.mvc._

object YourController extends Controller {
  def index = Action {
    Ok(VM("vm/index.vm", Map("name" -> "__name__", "title" -> "__title__")))
  }
}
```

## 3. Add `vm/index.vm` File.

```
<!DOCTYPE html>
<html>
    <head>
        <title>${title}</title>
    </head>
    <body>
        $name
    </body>
</html>
```

## 4. Run your application.

# Additional Configurations.

Plugin load `velocity.properties` in `velocity.jar`.

If customize its settings, add file `velocity_plugin.properties` and edit any settings.

# Scala Application Setting.

Velocity don't support Scala's ```Map``` and ```List``` usually.

If you want to use, please set the following in your project.

```
runtime.introspector.uberspect = jp.furyu.play.velocity.ScalaUberspect
```

# Sample Scala Play Project.

A sample application is available, to run it:

1. clone the repository
2. sbt or sbt.bat
3. In sbt console, enter `project scala-sample` and `run`
4. open a browser to [localhost:9000](http://localhost:9000)
