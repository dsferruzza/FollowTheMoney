package models

import play.api.db._
import play.api.Play.current
import play.api.libs.json._
import anorm._
import anorm.SqlParser._
import models.AnormType._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

case class Expense(id: Long, date: DateTime, id_category: Long, description: Option[String], amount: BigDecimal) {
	def readableDate = date.toString(DateTimeFormat.forPattern("dd/MM/yyyy"))
}

object Expense {
	val simple = {
		get[Long]("expense.id") ~
		get[DateTime]("expense.date") ~
		get[Long]("expense.id_category") ~
		get[Option[String]]("expense.description") ~
		get[java.math.BigDecimal]("expense.amount") map {
			case id~date~id_category~description~amount => Expense(id, date, id_category, description, BigDecimal(amount))
		}
	}

	val withCategory = Expense.simple ~ (Category.simple.?) map {
		case expense~category => (expense, category)
	}

	val nbPerPage = 10

	def count: Int = DB.withConnection { implicit connection =>
		SQL("SELECT COUNT(id) AS nb FROM expense").apply().head[Long]("nb").toInt
	}

	def nbPage: Int = (count / nbPerPage.toFloat).ceil.toInt

	def calculateLimit(page: Int): (Int, Int) = {
		val p = if (page.abs == 0) 1 else page.abs
		((p - 1) * nbPerPage, nbPerPage)
	}
	
	def getAll: List[Expense] = DB.withConnection { implicit connection =>
		SQL("SELECT id, date, id_category, description, amount FROM expense ORDER BY date DESC, id DESC").as(Expense.simple.*)
	}

	def getAllWithCategory: List[(Expense, Option[Category])] = DB.withConnection { implicit connection =>
		SQL("""
			SELECT e.id, e.date, e.id_category, e.description, e.amount, c.id, c.name
			FROM expense AS e
			LEFT JOIN category AS c ON c.id = e.id_category
			ORDER BY e.date DESC, e.id DESC
			""").as(Expense.withCategory.*)
	}

	def getSome(limit: (Int, Int)): List[Expense] = DB.withConnection { implicit connection =>
		SQL("SELECT id, date, id_category, description, amount FROM expense ORDER BY date DESC, id DESC OFFSET {start} LIMIT {nb}").on(
			'start -> limit._1,
			'nb -> limit._2
		).as(Expense.simple.*)
	}

	def getSomeWithCategory(limit: (Int, Int)): List[(Expense, Option[Category])] = DB.withConnection { implicit connection =>
		SQL("""
			SELECT e.id, e.date, e.id_category, e.description, e.amount, c.id, c.name
			FROM expense AS e
			LEFT JOIN category AS c ON c.id = e.id_category
			ORDER BY e.date DESC, e.id DESC
			OFFSET {start}
			LIMIT {nb}
			""").on(
			'start -> limit._1,
			'nb -> limit._2
		).as(Expense.withCategory.*)
	}

	def getPage(page: Int): List[Expense] = getSome(calculateLimit(page))

	def getPageWithCategory(page: Int): List[(Expense, Option[Category])] = getSomeWithCategory(calculateLimit(page))

	def findById(id: Long): Option[Expense] = DB.withConnection { implicit connection =>
		SQL("SELECT id, date, id_category, description, amount FROM expense WHERE id = {id}").on(
			'id -> id
		).as(Expense.simple.singleOpt)
	}

	def create(date: DateTime, id_category: Long, description: Option[String], amount: BigDecimal): Option[Long] = DB.withConnection { implicit connection =>
		SQL("INSERT INTO expense (date, id_category, description, amount) VALUES ({date}, {id_category}, {description}, {amount})").on(
			'date -> date,
			'id_category -> id_category,
			'description -> description,
			'amount -> amount.bigDecimal
		).executeInsert()
	}

	def edit(id: Long, date: DateTime, id_category: Long, description: Option[String], amount: BigDecimal): Boolean = DB.withConnection { implicit connection =>
		SQL("UPDATE expense SET date = {date}, id_category = {id_category}, description = {description}, amount = {amount} WHERE id = {id}").on(
			'id -> id,
			'date -> date,
			'id_category -> id_category,
			'description -> description,
			'amount -> amount.bigDecimal
		).execute()
	}

	def delete(id: Long): Boolean = DB.withConnection { implicit connection =>
		SQL("DELETE FROM expense WHERE id = {id}").on(
			'id -> id
		).execute()
	}

	implicit val expenseWrites = new Writes[Expense] {
		def writes(expense: Expense) = Json.obj(
			"id" -> expense.id,
			"date" -> expense.date.toString(DateTimeFormat.forPattern("yyyy-MM-dd")),
			"id_category" -> expense.id_category,
			"description" -> expense.description,
			"amount" -> expense.amount
		)
	}
}
