package controllers.helpers

import views.html.helper.FieldConstructor

object BootstrapInlineHelper {
	implicit val myFields = FieldConstructor(views.html.helper.bootstrapInlineInput.f)
}
