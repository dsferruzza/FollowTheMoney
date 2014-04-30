package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Category(id: Long, name: String)

object Category {
  val simple = {
    get[Long]("category.id") ~
    get[String]("category.name") map {
      case id~name => Category(id, name)
    }
  }
  
  def getAll(): List[Category] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT id, name FROM category ORDER BY name ASC").as(Category.simple.*)
    }
  }
}
