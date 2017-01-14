package model.messaging.response

import model.messaging.Message

/**
	* Created by Katarzyna Herman on 03.11.16.
	*/
case class OkMessage(override val id: Long) extends Message {

}
