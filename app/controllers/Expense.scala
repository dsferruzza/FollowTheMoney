package controllers

import play.api._
import play.api.mvc._

object Expense extends Controller {

	def index = Action {
		Ok(views.html.expense(models.Expense.getAll))
	}

}
