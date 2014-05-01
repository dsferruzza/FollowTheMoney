package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation._
import org.joda.time.DateTime

object Expense extends Controller {

	case class ExpenseData(date: DateTime, id_category: Long, description: Option[String], amount: BigDecimal)

	val defaultDate = new DateTime()
	val expenseForm = Form(
		mapping(
			"date" -> default(jodaDate, defaultDate),
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

	def add = Action { implicit request =>
		expenseForm.bindFromRequest.fold(
			errors => {
				val cat = models.Category.getAll.map(c => (c.id.toString, c.name))
				BadRequest(views.html.expenseAdd(errors, cat))
			},
			data => {
				???
			}
		)
	}

}
