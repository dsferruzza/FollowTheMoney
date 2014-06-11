package controllers

import play.api._
import play.api.mvc._

object Analyze extends Controller {

	def index = Action {
		Ok(views.html.analyze(models.MonthlyReport.getAll))
	}

}
