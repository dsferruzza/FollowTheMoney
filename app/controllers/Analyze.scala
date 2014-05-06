package controllers

import play.api._
import play.api.mvc._

object Analyze extends Controller {

	case class YearSummary(year: Long, amount: Float, items: List[MonthSummary])
	case class MonthSummary(year: Long, month: Long, amount: Float, items: List[models.MonthlyReport])

	def index = Action {
		val monthlyReport: List[YearSummary] =
			models.MonthlyReport.getAll
				.groupBy(_.year)
				.map { i =>
					val monthSummaries = i._2
							.groupBy(_.month)
							.map { j =>
								val amount = j._2.map(_.amount).sum
								MonthSummary(j._2.head.year, j._2.head.month, amount, j._2.sortBy(_.category))
							}
							.toList
							.sortBy(_.month)
					val year = i._2.head.year
					val amount = i._2.map(_.amount).sum
					YearSummary(year, amount, monthSummaries)
				}
				.toList
				.sortBy(_.year)
		Ok(views.html.analyze(monthlyReport))
	}

}
