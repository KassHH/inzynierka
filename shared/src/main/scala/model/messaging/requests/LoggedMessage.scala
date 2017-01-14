package model.messaging.requests

import model.messaging.Message

/**
	* Created by Katarzyna Herman on 30.10.16.
	*/
case class LoggedMessage(override val id: Long) extends Message {
}
