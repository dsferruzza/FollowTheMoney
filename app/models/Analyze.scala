package models

import play.api.db._
import play.api.Play.current
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

	def getAll: List[MonthlyReport] = {
		DB.withConnection { implicit connection =>
			SQL("""
				SELECT EXTRACT(YEAR FROM e.date)::integer AS "year", EXTRACT(MONTH FROM e.date)::integer AS "month", MIN(e.id_category) AS "id_category", c.name AS "category", SUM(e.amount) AS "amount"
				FROM expense AS e
				INNER JOIN category AS c ON e.id_category = c.id
				GROUP BY "year", "month", c.name
				ORDER BY "year" ASC, "month" ASC, "category" ASC
				""").as(MonthlyReport.simple.*)
		}
	}
}
