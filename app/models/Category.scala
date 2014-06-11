package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Category(id: Long, name: String)

object Category {
	val nbTopCategories = 3

	val simple = {
		get[Long]("category.id") ~
		get[String]("category.name") map {
			case id~name => Category(id, name)
		}
	}
	
	def getAll: List[Category] = DB.withConnection { implicit connection =>
		SQL("SELECT id, name FROM category ORDER BY name ASC").as(Category.simple.*)
	}

	def getTopCategories: List[Category] = DB.withConnection { implicit connection =>
		SQL("""
			SELECT c.id, c.name
			FROM category AS c
			INNER JOIN expense AS e ON c.id = e.id_category
			GROUP BY c.id
			ORDER BY COUNT(e.id) DESC
			LIMIT {nb}
			""").on(
				'nb -> nbTopCategories
			).as(Category.simple.*)
	}

	def getAllForSelect: List[(String, String)] = getAll.map(c => (c.id.toString, c.name))

	def getTopCategoriesForSelect: List[(String, String)] = getTopCategories.map(c => (c.id.toString, c.name))

	def getAllForSelectWithTopCategories: List[(String, String)] = getTopCategoriesForSelect ++ (("", "") +: getAllForSelect)

	def create(name: String): Option[Long] = DB.withConnection { implicit connection =>
		SQL("INSERT INTO category (name) VALUES ({name})").on(
			'name -> name
		).executeInsert()
	}

	def delete(id: Long): Boolean = DB.withConnection { implicit connection =>
		SQL("DELETE FROM category WHERE id = {id}").on(
			'id -> id
		).execute()
	}
}
