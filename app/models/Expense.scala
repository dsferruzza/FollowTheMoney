package models

import play.api.db._
import play.api.Play.current
import play.api.libs.json._
import anorm._
import anorm.SqlParser._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/** An event in which the user paid something
 * @param id Unique ID for storage
 * @param date Date of the event
 * @param id_category ID of the related Category item
 * @param description Short description of the event
 * @param amount Amount paid
 */
case class Expense(id: Long, date: DateTime, id_category: Long, description: Option[String], amount: BigDecimal) {
	/** Date in french format */
	lazy val readableDate = date.toString(DateTimeFormat.forPattern("dd/MM/yyyy"))
	/** Date in ISO 8601 format */
	lazy val iso8601Date = date.toString(DateTimeFormat.forPattern("yyyy-MM-dd"))
}

object Expense {
	/** Simple row parser */
	val simple = {
		get[Long]("expense.id") ~
		get[DateTime]("expense.date") ~
		get[Long]("expense.id_category") ~
		get[Option[String]]("expense.description") ~
		get[java.math.BigDecimal]("expense.amount") map {
			case id~date~id_category~description~amount => Expense(id, date, id_category, description, BigDecimal(amount))
		}
	}

	/** Row parser for Expense and Category */
	val withCategory = Expense.simple ~ (Category.simple.?) map {
		case expense~category => (expense, category)
	}

	/** Number of rows per page */
	val nbPerPage = 10

	/** Get the total number of expenses */
	def count: Int = DB.withConnection { implicit connection =>
		SQL"SELECT COUNT(id) AS nb FROM expense".apply().head[Long]("nb").toInt
	}

	/** Determine the number of pages */
	def nbPage: Int = (count / nbPerPage.toFloat).ceil.toInt

	/** Determine the values to give to OFFSET and LIMIT
	 * @param page Current page
	 * @return (OFFSET, LIMIT)
	 */
	def calculateLimit(page: Int): (Int, Int) = {
		val p = if (page.abs == 0) 1 else page.abs
		((p - 1) * nbPerPage, nbPerPage)
	}
	
	/** Get all items */
	def getAll: List[Expense] = DB.withConnection { implicit connection =>
		SQL"SELECT id, date, id_category, description, amount FROM expense ORDER BY date DESC, id DESC".as(Expense.simple.*)
	}

	/** Get all Expense and their Category */
	def getAllWithCategory: List[(Expense, Option[Category])] = DB.withConnection { implicit connection =>
		SQL"""
			SELECT e.id, e.date, e.id_category, e.description, e.amount, c.id, c.name
			FROM expense AS e
			LEFT JOIN category AS c ON c.id = e.id_category
			ORDER BY e.date DESC, e.id DESC
			""".as(Expense.withCategory.*)
	}

	/** Get some of the Expense
	 * @param limit Values for OFFSET and LIMIT
	 * @see calculateLimit
	 */
	def getSome(limit: (Int, Int)): List[Expense] = DB.withConnection { implicit connection =>
		val (start, nb) = limit
		SQL"SELECT id, date, id_category, description, amount FROM expense ORDER BY date DESC, id DESC OFFSET ${start} LIMIT ${nb}".as(Expense.simple.*)
	}

	/** Get some of the Expense and their Category
	 * @param limit Values for OFFSET and LIMIT
	 * @see calculateLimit
	 */
	def getSomeWithCategory(limit: (Int, Int)): List[(Expense, Option[Category])] = DB.withConnection { implicit connection =>
		val (start, nb) = limit
		SQL"""
			SELECT e.id, e.date, e.id_category, e.description, e.amount, c.id, c.name
			FROM expense AS e
			LEFT JOIN category AS c ON c.id = e.id_category
			ORDER BY e.date DESC, e.id DESC
			OFFSET ${start}
			LIMIT ${nb}
		""".as(Expense.withCategory.*)
	}

	/** Get Expense for the current page
	 * @param page Current page
	 */
	def getPage(page: Int): List[Expense] = getSome(calculateLimit(page))

	/** Get Expense and their Category for the current page
	 * @param page Current page
	 */
	def getPageWithCategory(page: Int): List[(Expense, Option[Category])] = getSomeWithCategory(calculateLimit(page))

	/** Get an item by its ID */
	def findById(id: Long): Option[Expense] = DB.withConnection { implicit connection =>
		SQL"SELECT id, date, id_category, description, amount FROM expense WHERE id = ${id}".as(Expense.simple.singleOpt)
	}

	/** Create an item */
	def create(date: DateTime, id_category: Long, description: Option[String], amount: BigDecimal): Option[Long] = DB.withConnection { implicit connection =>
		SQL"INSERT INTO expense (date, id_category, description, amount) VALUES (${date}, ${id_category}, ${description}, ${amount})".executeInsert()
	}

	/** Edit an item */
	def edit(id: Long, date: DateTime, id_category: Long, description: Option[String], amount: BigDecimal): Boolean = DB.withConnection { implicit connection =>
		SQL"UPDATE expense SET date = ${date}, id_category = ${id_category}, description = ${description}, amount = ${amount} WHERE id = ${id}".execute()
	}

	/** Delete an item */
	def delete(id: Long): Boolean = DB.withConnection { implicit connection =>
		SQL"DELETE FROM expense WHERE id = ${id}".execute()
	}

	/** JSON Writes */
	// JSON inception is not used because the "date" field should returns the ISO 8601 date
	implicit val expenseWrites = new Writes[Expense] {
		def writes(expense: Expense) = Json.obj(
			"id" -> expense.id,
			"date" -> expense.iso8601Date,
			"id_category" -> expense.id_category,
			"description" -> expense.description,
			"amount" -> expense.amount
		)
	}
}
