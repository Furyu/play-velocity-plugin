package jp.furyu.play.velocity

import java.io.StringWriter
import java.util.{ Iterator => JavaIterator, Properties }

import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.log.Log
import org.apache.velocity.runtime.parser.node.MapGetExecutor
import org.apache.velocity.runtime.parser.node.PropertyExecutor
import org.apache.velocity.util.introspection.UberspectImpl.VelGetterImpl
import org.apache.velocity.util.introspection.Info
import org.apache.velocity.util.introspection.Introspector
import org.apache.velocity.util.introspection.UberspectImpl
import org.apache.velocity.util.introspection.VelPropertyGet

import play.api.templates.Html
import play.api.Application
import play.api.Plugin
import org.apache.velocity.runtime.resource.{ Resource, ResourceManagerImpl }
import util.DynamicVariable
import play.api.mvc.RequestHeader
import org.apache.velocity.VelocityContext
import org.apache.velocity.util.ClassUtils

/**
 * Velocity Plugin for Play2!
 *
 * @see http://www.frothandjava.com/2010/02/scala-spring-templates-velocity.html
 */
class VelocityPlugin(app: Application) extends Plugin {

  lazy val engine: VelocityEngine = {
    // initialize velocity engine
    val prop = new Properties
    val is = this.getClass().getResourceAsStream("/" + VelocityPlugin.RuntimePropertiesFileName)
    if (is != null) {
      VelocityPlugin.Logger.info("setup engine in [%s]".format(VelocityPlugin.RuntimePropertiesFileName))
      prop.load(is)
    }

    val engine = new VelocityEngine(prop)
    engine.init

    engine
  }

  override def onStart() {
    VelocityPlugin.Logger.info("initialize engine")
    engine
  }

  override val enabled: Boolean = true
}
object VelocityPlugin {
  lazy val Logger = play.api.Logger("jp.furyu.play.velocity.VelocityPlugin")
  val RuntimePropertiesFileName = "velocity_plugin.properties"

  def current: VelocityPlugin = play.api.Play.current.plugin[VelocityPlugin].getOrElse(throw new IllegalStateException("VelocityPlugin not installed"))
}

package object mvc {

  /**
   * marge velocity template to Html.
   *
   * @param templatePath relative path of template file to "file.resource.loader.path"
   * @param attributes request attributes (default empty)
   * @param charset encoding template charset (default utf-8)
   * @return Html
   * @throws ResourceNotFoundException not found template file
   * @throws ParseErrorException template invalid velocity format
   * @throws MethodInvocationException error occur when evaluate template in object of context
   */
  def VM(templatePath: String, attributes: Map[String, Any] = Map.empty, charset: String = "utf-8")(implicit request: RequestHeader): Html = {
    // set request to threadlocal value
    RequestContext.current.value = Some(RequestContext(request))

    // create context and set attributes
    val context = new VelocityContext
    attributes.foreach { case (key, value) => context.put(key, value) }

    // evaluate template by velocity
    val writer = new StringWriter
    VelocityPlugin.current.engine.mergeTemplate(templatePath, charset, context, writer)

    // wrap Html
    Html(writer.toString)
  }
}

/**
 * Uberspect for Scala.
 *
 * <p>
 * Velocity uses introspection/reflection to access properties and methods on an object.<br>
 * Uberspect have the responsibility of it.<br>
 * This implementation of Uberspect customize for Scala.
 * </p>
 */
class ScalaUberspect extends UberspectImpl {

  import jp.furyu.play.velocity.ScalaUberspect.{ ScalaMapGetExecutor, ScalaPropertyExecutor }

  override def getIterator(obj: java.lang.Object, i: Info): JavaIterator[_] = {
    def makeJavaIterator(iter: Iterator[_]) = new JavaIterator[AnyRef] {
      override def hasNext() = iter.hasNext
      override def next() = iter.next().asInstanceOf[AnyRef]
      override def remove() = throw new java.lang.UnsupportedOperationException("Remove not supported")
    }

    obj match {
      case i: Iterable[_] => makeJavaIterator(i.iterator)
      case i: Iterator[_] => makeJavaIterator(i)
      case _ => super.getIterator(obj, i)
    }
  }

  override def getPropertyGet(obj: java.lang.Object, identifier: String, i: Info): VelPropertyGet = {
    if (obj != null) {
      val claz = obj.getClass()

      val executor = obj match {
        case m: Map[_, _] => new ScalaMapGetExecutor(log, claz, identifier)
        case _ => new ScalaPropertyExecutor(log, introspector, claz, identifier)
      }

      if (executor.isAlive) {
        new VelGetterImpl(executor)
      } else {
        super.getPropertyGet(obj, identifier, i)
      }
    } else {
      null
    }
  }
}
object ScalaUberspect {

  private class ScalaPropertyExecutor(log: Log, introspector: Introspector, clazz: java.lang.Class[_], property: String) extends PropertyExecutor(log, introspector, clazz, property) {
    override def discover(clazz: java.lang.Class[_], property: String) = {
      setMethod(introspector.getMethod(clazz, property, Array[java.lang.Object]()))
      if (!isAlive()) {
        super.discover(clazz, property)
      }
    }
  }

  private class ScalaMapGetExecutor(val llog: Log, val clazz: java.lang.Class[_], val property: String) extends MapGetExecutor(llog, clazz, property) {
    override def isAlive = true
    override def execute(o: AnyRef) = o.asInstanceOf[Map[String, AnyRef]].getOrElse[AnyRef](property, null).asInstanceOf[java.lang.Object]
  }
}

/**
 * This is abstract class of the resource finder, should extend.
 */
trait ResourceFinder {

  /**
   * rewrite resourceName by request.
   *
   * @param request
   * @param resourceName relative path of resource
   * @return rewrited relative path of resource
   */
  def find(request: RequestHeader, resourceName: String): String
}

/**
 * This is extended @{link ResourceManagerImpl} to be able to rewrite resource path by request.
 */
class RequestRewritableResourceManagerImpl extends ResourceManagerImpl {

  private lazy val ResourceFinderClassPropertyKey: String = "resource.finder.class"

  /**
   * rewrite resourceName by request.<br>
   * need to implement @{link ResourceFinder} and set <tt>resource.finder.class</tt> to properties.
   *
   * @param request
   * @param resourceName relative path of resource
   * @return rewrited relative path of resource
   */
  protected def rewriteResource(request: RequestHeader, resourceName: String): String = {
    lazy val resourceFinderClassName = VelocityPlugin.current
      .engine.getProperty(ResourceFinderClassPropertyKey).asInstanceOf[String]

    scala.util.control.Exception.allCatch opt ClassUtils.getNewInstance(resourceFinderClassName) match {
      case Some(finder) if finder.isInstanceOf[ResourceFinder] => {
        finder.asInstanceOf[ResourceFinder].find(request, resourceName)
      }
      case _ => {
        VelocityPlugin.Logger.warn("ResourceFinder not found or plugin setting is invalid.")
        resourceName
      }
    }
  }

  override def getResource(resourceName: String, resourceType: Int, encoding: String): Resource = {
    val rewritedResourceName = RequestContext.current.value match {
      case Some(v) => rewriteResource(v.request, resourceName)
      case None => resourceName // do nothing.
    }
    super.getResource(rewritedResourceName, resourceType, encoding)
  }
}

private case class RequestContext(request: RequestHeader)
private object RequestContext {
  val current = new DynamicVariable[Option[RequestContext]](None)
}
