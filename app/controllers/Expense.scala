package controllers

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation._
import play.api.data.validation.Constraints._
import play.api.i18n.Messages.Implicits._
import play.api.mvc._
import play.api.Play.current
import org.joda.time.DateTime

object Expense extends Controller {

	case class ExpenseData(date: DateTime, id_category: Long, description: Option[String], amount: BigDecimal)

	def expenseForm = Form(	// "def" instead of "val" so the default date is build every time
		mapping(
			"date" -> default(jodaDate, new DateTime),
			"id_category" -> longNumber,
			"description" -> optional(text),
			"amount" -> bigDecimal.verifying(min(BigDecimal(0)))
		)(ExpenseData.apply)(ExpenseData.unapply)
	)

	def index(page: Int) = Action {
		Ok(views.html.expense(models.Expense.getPageWithCategory(page), (page, models.Expense.nbPage)))
	}

	def addForm = Action {
		Ok(views.html.expenseAdd(expenseForm, models.Category.getAllForSelectWithTopCategories))
	}

	def add = Action { implicit request =>
		expenseForm.bindFromRequest.fold(
			errors => {
				BadRequest(views.html.expenseAdd(errors, models.Category.getAllForSelectWithTopCategories))
			},
			data => {
				models.Expense.create(data.date, data.id_category, data.description, data.amount)
				Redirect(routes.Expense.index())
			}
		)
	}

	def editForm(id: Long) = Action {
		val expense = models.Expense.findById(id)
		val form = expense match {
			case Some(e) => {
				val ed = new ExpenseData(e.date, e.id_category, e.description, e.amount)
				expenseForm.fill(ed)
			}
			case None => expenseForm
		}
		Ok(views.html.expenseEdit(id, form, models.Category.getAllForSelectWithTopCategories))
	}

	def edit(id: Long) = Action { implicit request =>
		expenseForm.bindFromRequest.fold(
			errors => {
				BadRequest(views.html.expenseEdit(id, errors, models.Category.getAllForSelectWithTopCategories))
			},
			data => {
				models.Expense.edit(id, data.date, data.id_category, data.description, data.amount.toFloat)
				Redirect(routes.Expense.index())
			}
		)
	}

	def delete(id: Long) = Action {
		models.Expense.delete(id)
		Redirect(routes.Expense.index())
	}

}
