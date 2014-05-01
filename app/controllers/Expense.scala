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
				models.Expense.create(data.date, data.id_category, data.description, data.amount.toFloat)
				Redirect(routes.Expense.index)
			}
		)
	}

	def editForm(id: Long) = Action {
		val cat = models.Category.getAll.map(c => (c.id.toString, c.name))
		val expense = models.Expense.findById(id)
		val form = expense match {
			case Some(e) => {
				val ed = new ExpenseData(e.date, e.id_category, e.description, BigDecimal(e.amount))
				expenseForm.fill(ed)
			}
			case None => expenseForm
		}
		Ok(views.html.expenseEdit(id, form, cat))
	}

	def edit(id: Long) = TODO

}
