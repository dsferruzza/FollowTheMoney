package controllers

import play.api._
import play.api.mvc._

object Category extends Controller {

  def index = Action {
    Ok(views.html.category())
  }

}
