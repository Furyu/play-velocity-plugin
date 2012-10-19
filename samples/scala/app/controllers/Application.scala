package controllers

import play.api.mvc._
import jp.furyu.play.velocity.mvc.VM

object Application extends Controller {

  def index = Action { implicit request =>
    val args = Map(
      "name" -> "hoge",
      "link" -> new LinkTool,
      "user" -> User(100L, "__name__"),
      "users" -> (1 to 10).map { i => User(i, "__name%d__".format(i)) },
      "i" -> 1)

    Ok(VM("vm/template.vm", args))
  }

  def index2(kind: String, index: Int) = Action { implicit request =>
    val args = Map(
      "kind" -> kind,
      "index" -> index)
    Ok(VM("vm/template2.vm", args))
  }

  def index3 = Action { implicit request =>
    Ok(VM("vm/template3.vm"))
  }
}

class LinkTool() {
  def getHoge: String = "__hoge__"
  def fuga: String = "__fuga__"
  override def toString = "__LinkTool__"
}

case class User(id: Long, name: String)

class ResourceFinderImpl extends jp.furyu.play.velocity.ResourceFinder {
  def find(request: RequestHeader, resourceName: String): String = {
    jp.furyu.play.velocity.VelocityPlugin.Logger.trace("ResourceFinderImpl#find resourceName[%s], request.uri[%s]".format(resourceName, request.uri))
    resourceName
  }
}
