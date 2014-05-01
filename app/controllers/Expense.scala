package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

object Expense extends Controller {

	case class ExpenseData(date: String, id_category: Long, description: Option[String], amount: BigDecimal)

	val expenseForm = Form(
		mapping(
			"date" -> text,
			"id_category" -> longNumber,
			"description" -> optional(text),
			"amount" -> bigDecimal
		)(ExpenseData.apply)(ExpenseData.unapply)
	)

	def index = Action {
		Ok(views.html.expense(models.Expense.getAllWithCategory))
	}

	def addForm = Action {
		val cat = models.Category.getAll.map(c => (c.id.toString, c.name))
		Ok(views.html.expenseAdd(expenseForm, cat))
	}

	def add = TODO

}
