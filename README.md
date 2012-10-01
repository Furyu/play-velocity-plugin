# Velocity Plugin for Play2 framework
## What is Velocity?

See [Apache Velocity Site](http://velocity.apache.org/)

## Usage.

### 1. Install Plugin.

Edit file `project/Build.scala`

```
libraryDependencies ++= Seq(
  "jp.furyu" %% "play-velocity-plugin" % "LATEST_VERSION"
)
```

Edit file `conf/play.plugins`

```
5000:jp.furyu.play.velocity.VelocityPlugin
```

### 2. Call `VM` Function in your Controller.

```
object YourController extends Controller {
  def index = Action {
    Ok(VM("vm/index.vm", Map("name" -> "__name__", "title" -> "__title__")))
  }
}
```

### 3. Add `vm/index.vm` File.

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

### 4. Run your application.

## Additional Configurations.

Plugin load `velocity.properties` in `velocity.jar`.

If customize its settings, add file `velocity_plugin.properties` and edit any settings.

### Scala Application Setting.

```
runtime.introspector.uberspect = jp.furyu.play.velocity.ScalaUberspect
```

## Sample Scala Play Project.

A sample application is available, to run it:

1. clone the repository
2. sbt or sbt.bat
3. In sbt console, enter `project scala-sample` and `run`
4. open a browser to [localhost:9000](http://localhost:9000)
