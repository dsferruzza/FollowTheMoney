package models

import play.api.db._
import play.api.Play.current
import play.api.libs.json._
import anorm._
import anorm.SqlParser._
import models.AnormType._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

case class MonthlyReport(year: Long, month: Long, id_category: Long, category: String, amount: BigDecimal)

object MonthlyReport {
	val simple = {
		get[Long]("year") ~
		get[Long]("month") ~
		get[Long]("id_category") ~
		get[String]("category") ~
		get[java.math.BigDecimal]("amount") map {
			case year~month~id_category~category~amount => MonthlyReport(year, month, id_category, category, BigDecimal(amount))
		}
	}

	case class YearSummary(year: Long, amount: BigDecimal, items: List[MonthSummary])
	case class MonthSummary(year: Long, month: Long, amount: BigDecimal, items: List[models.MonthlyReport])

	def getAll: List[YearSummary] = {
		val output = DB.withConnection { implicit connection =>
			SQL("""
				SELECT EXTRACT(YEAR FROM e.date)::integer AS "year", EXTRACT(MONTH FROM e.date)::integer AS "month", MIN(e.id_category) AS "id_category", c.name AS "category", SUM(e.amount) AS "amount"
				FROM expense AS e
				INNER JOIN category AS c ON e.id_category = c.id
				GROUP BY "year", "month", c.name
				ORDER BY "year" ASC, "month" ASC, "category" ASC
				""").as(MonthlyReport.simple.*)
		}
		output.groupBy(_.year)
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
	}

	implicit val yearSummaryWrites = new Writes[YearSummary] {
		def writes(ys: YearSummary) = Json.obj(
			"year" -> ys.year,
			"amount" -> ys.amount,
			"items" -> ys.items
		)
	}

	implicit val monthSummaryWrites: Writes[MonthSummary] = new Writes[MonthSummary] {
		def writes(ms: MonthSummary) = Json.obj(
			"year" -> ms.year,
			"month" -> ms.month,
			"amount" -> ms.amount,
			"items" -> ms.items
		)
	}

	implicit val monthlyReportWrites: Writes[MonthlyReport] = new Writes[MonthlyReport] {
		def writes(mr: MonthlyReport) = Json.obj(
			"year" -> mr.year,
			"month" -> mr.month,
			"id_category" -> mr.id_category,
			"category" -> mr.category,
			"amount" -> mr.amount
		)
	}
}
