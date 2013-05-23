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
package controllers

import play.api.mvc._
import jp.furyu.play.velocity.mvc.VM

object Application extends Controller {

  def index = Action {
    val args = Map(
      "name" -> "hoge",
      "link" -> new LinkTool,
      "user" -> User(100L, "__name__"),
      "users" -> (1 to 10).map { i => User(i, "__name%d__".format(i)) },
      "i" -> 1)

    Ok(VM("vm/template.vm", args))
  }

  def index2(kind: String, index: Int) = Action {
    val args = Map(
      "kind" -> kind,
      "index" -> index)
    Ok(VM("vm/template2.vm", args))
  }

  def index3 = Action {
    Ok(VM("vm/template3.vm"))
  }
}

class LinkTool() {
  def getHoge: String = "__hoge__"
  def fuga: String = "__fuga__"
  override def toString = "__LinkTool__"
}

case class User(id: Long, name: String)
