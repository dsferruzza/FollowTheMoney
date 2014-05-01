package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import models.AnormType._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

case class Expense(id: Long, date: Option[DateTime], id_category: Option[Long], description: Option[String], amount: Float) {
	def readableDate = date.map(_.toString(DateTimeFormat.forPattern("dd/MM/yyyy")))
}

object Expense {
	val simple = {
		get[Long]("expense.id") ~
		get[Option[DateTime]]("expense.date") ~
		get[Option[Long]]("expense.id_category") ~
		get[Option[String]]("expense.description") ~
		get[Float]("expense.amount") map {
			case id~date~id_category~description~amount => Expense(id, date, id_category, description, amount)
		}
	}
	
	def getAll(): List[Expense] = {
		DB.withConnection { implicit connection =>
			SQL("SELECT id, date, id_category, description, amount FROM expense ORDER BY date DESC").as(Expense.simple.*)
		}
	}
}
