package model.messaging.requests

import model.messaging.Message

/**
	* Created by kass on 18.10.16.
	*/
case class TextMessage(override val id: Long, var text: String, talkId: Long) extends Message {
}
