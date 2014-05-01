package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

object Category extends Controller {

  val categoryForm = Form(
    "name" -> nonEmptyText
  )

  def index = Action {
    Ok(views.html.category(models.Category.getAll, categoryForm))
  }

  def add = Action { implicit request =>
    categoryForm.bindFromRequest.fold(
      errors => {
        BadRequest(views.html.category(models.Category.getAll, errors))
      },
      data => {
        models.Category.create(data)
        Redirect(routes.Category.index)
      }
    )
  }

  def delete(id: Long) = TODO

}
