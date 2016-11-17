package model.messaging.response

import model.messaging.Message

/**
	* Created by kass on 03.11.16.
	*/
case class CheckMessage(override val id: Long, check: Boolean) extends Message {
	def getCheck: Boolean = check
}
