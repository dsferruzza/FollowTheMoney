package controllers

import play.api._
import play.api.i18n.Messages.Implicits._
import play.api.mvc._
import play.api.Play.current

object Application extends Controller {

	def index = Action {
		Ok(views.html.index(controllers.Expense.expenseForm, models.Category.getAllForSelectWithTopCategories))
	}

}
