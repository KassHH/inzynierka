package view

import scalafx.scene.control.CheckBox

/**
	* Created by Katarzyna Herman on 20.11.16.
	*/
class UserCheck(id: Long, checkBox: CheckBox) {
	val check: CheckBox = checkBox
	val userId: Long = id
}
