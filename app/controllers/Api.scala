package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._

object Api extends Controller {

	def export = Action {
		val outputJson = Json.obj(
			"monthlyReport" -> models.MonthlyReport.getAll,
			"categories" -> models.Category.getAll,
			"expenses" -> models.Expense.getAll
		)
		Ok(Json.toJson(outputJson))
	}

}
