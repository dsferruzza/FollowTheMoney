package controllers.helpers

import views.html.helper.FieldConstructor

object bootstrapHorizontalHelper {
	implicit val myFields = FieldConstructor(views.html.helper.bootstrapHorizontalInput.f)
}
