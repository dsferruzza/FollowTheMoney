package models

import play.api.db._
import play.api.Play.current
import play.api.libs.json._
import anorm._
import anorm.SqlParser._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/** A report of expenses for a given month and a given Category
 * @param year The year of the report
 * @param month The month of the report
 * @param id_category ID of the related Category item
 * @param category Displayed name of the related Category item
 * @param amount Total amount paid in that context
 */
case class MonthlyReport(year: Long, month: Long, id_category: Long, category: String, amount: BigDecimal)

object MonthlyReport {
	/** Simple row parser */
	val simple = {
		get[Long]("year") ~
		get[Long]("month") ~
		get[Long]("id_category") ~
		get[String]("category") ~
		get[java.math.BigDecimal]("amount") map {
			case year~month~id_category~category~amount => MonthlyReport(year, month, id_category, category, BigDecimal(amount))
		}
	}

	/** A collection of reports for a given year
	 * @param amount Total amount paid in that context
	 * @param items Collection of reports for the differents months of this year
	 */
	case class YearSummary(year: Long, amount: BigDecimal, items: List[MonthSummary])
	object YearSummary {
		/** JSON Writes */
		implicit val yearSummaryWrites: Writes[YearSummary] = Json.writes[YearSummary]
	}

	/** A collection of reports for a given month
	 * @param amount Total amount paid in that context
	 * @param items Collection of month reports for the differents Category
	 */
	case class MonthSummary(year: Long, month: Long, amount: BigDecimal, items: List[models.MonthlyReport])
	object MonthSummary {
		/** JSON Writes */
		implicit val monthSummaryWrites: Writes[MonthSummary] = Json.writes[MonthSummary]
	}

	/** Get all items */
	def fetchAll: List[MonthlyReport] = DB.withConnection { implicit connection =>
		SQL"""
			SELECT EXTRACT(YEAR FROM e.date)::integer AS "year", EXTRACT(MONTH FROM e.date)::integer AS "month", MIN(e.id_category) AS "id_category", c.name AS "category", SUM(e.amount) AS "amount"
			FROM expense AS e
			INNER JOIN category AS c ON e.id_category = c.id
			GROUP BY "year", "month", c.name
			ORDER BY "year" ASC, "month" ASC, "category" ASC
			""".as(MonthlyReport.simple.*)
	}

	/** Get all the collections of reports */
	def getAll: List[YearSummary] = {
		fetchAll.groupBy(_.year)
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

	/** JSON Writes */
	implicit val monthlyReportWrites = Json.writes[MonthlyReport]
}
