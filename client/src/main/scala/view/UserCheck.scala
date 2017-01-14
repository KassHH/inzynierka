package view

import scalafx.beans.property.BooleanProperty

/**
	* Created by Katarzyna Herman on 20.11.16.
	*/
class UserCheck(name: String, id: Long) {
	val selected = BooleanProperty(false)

	override def toString: String = name

	def getId: Long = id

	def getName: String = name

}
