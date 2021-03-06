package models

import play.api.db._
import play.api.Play.current
import play.api.libs.json._
import anorm._
import anorm.SqlParser._

/** A category of expense
 * @param id Unique ID for storage
 * @param name Displayed name
 */
case class Category(id: Long, name: String)

object Category {
	/** Simple row parser */
	val simple = {
		get[Long]("category.id") ~
		get[String]("category.name") map {
			case id~name => Category(id, name)
		}
	}
	
	/** Get all items */
	def getAll: List[Category] = DB.withConnection { implicit connection =>
		SQL"SELECT id, name FROM category ORDER BY name ASC".as(Category.simple.*)
	}

	/** Number of the most used items to get */
	val nbTopCategories = 3

	/** Get the most used items */
	def getTopCategories: List[Category] = DB.withConnection { implicit connection =>
		SQL"""
			SELECT c.id, c.name
			FROM category AS c
			INNER JOIN expense AS e ON c.id = e.id_category
			GROUP BY c.id
			ORDER BY COUNT(e.id) DESC
			LIMIT ${nbTopCategories}
		""".as(Category.simple.*)
	}

	/** Get all items to put them in option html tags
	 * @return A list of (id, display name)
	 */
	def getAllForSelect: List[(String, String)] = getAll.map(c => (c.id.toString, c.name))

	/** Get the most used items to put them in option html tags
	 * @return A list of (id, display name)
	 */
	def getTopCategoriesForSelect: List[(String, String)] = getTopCategories.map(c => (c.id.toString, c.name))

	/** Get the most used items, an empty line, and all items to put them in option html tags
	 * @return A list of (id, display name)
	 */
	def getAllForSelectWithTopCategories: List[(String, String)] = getTopCategoriesForSelect ++ (("", "") +: getAllForSelect)

	/** Create an item */
	def create(name: String): Option[Long] = DB.withConnection { implicit connection =>
		SQL"INSERT INTO category (name) VALUES (${name})".executeInsert()
	}

	/** Delete an item */
	def delete(id: Long): Boolean = DB.withConnection { implicit connection =>
		SQL"DELETE FROM category WHERE id = ${id}".execute()
	}

	/** JSON Writes */
	implicit val categoryWrites = Json.writes[Category]
}
