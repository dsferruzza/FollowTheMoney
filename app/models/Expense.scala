package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import models.AnormType._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

case class Expense(id: Long, date: DateTime, id_category: Long, description: Option[String], amount: Float) {
	def readableDate = date.toString(DateTimeFormat.forPattern("dd/MM/yyyy"))
}

object Expense {
	val simple = {
		get[Long]("expense.id") ~
		get[DateTime]("expense.date") ~
		get[Long]("expense.id_category") ~
		get[Option[String]]("expense.description") ~
		get[Float]("expense.amount") map {
			case id~date~id_category~description~amount => Expense(id, date, id_category, description, amount)
		}
	}

	val withCategory = Expense.simple ~ (Category.simple.?) map {
		case expense~category => (expense, category)
	}
	
	def getAll(): List[Expense] = {
		DB.withConnection { implicit connection =>
			SQL("SELECT id, date, id_category, description, amount FROM expense ORDER BY date DESC, id ASC").as(Expense.simple.*)
		}
	}

	def getAllWithCategory(): List[(Expense, Option[Category])] = {
		DB.withConnection { implicit connection =>
			SQL("""
				SELECT e.id, e.date, e.id_category, e.description, e.amount, c.id, c.name
				FROM expense AS e
				LEFT JOIN category AS c ON c.id = e.id_category
				ORDER BY e.date DESC, e.id ASC
				""").as(Expense.withCategory.*)
		}
	}

	def findById(id: Long): Option[Expense] = {
		DB.withConnection { implicit connection =>
			SQL("SELECT id, date, id_category, description, amount FROM expense WHERE id = {id}").on(
				'id -> id
			).as(Expense.simple.singleOpt)
		}
	}

	def create(date: DateTime, id_category: Long, description: Option[String], amount: Float): Option[Long] = {
		DB.withConnection { implicit connection =>
			SQL("INSERT INTO expense (date, id_category, description, amount) VALUES ({date}, {id_category}, {description}, {amount})").on(
				'date -> date,
				'id_category -> id_category,
				'description -> description,
				'amount -> amount
			).executeInsert()
		}
	}

	def edit(id: Long, date: DateTime, id_category: Long, description: Option[String], amount: Float): Boolean = {
		DB.withConnection { implicit connection =>
			SQL("UPDATE expense SET date = {date}, id_category = {id_category}, description = {description}, amount = {amount} WHERE id = {id}").on(
				'id -> id,
				'date -> date,
				'id_category -> id_category,
				'description -> description,
				'amount -> amount
			).execute()
		}
	}
}
