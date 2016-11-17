package model.messaging.response

import model.messaging.Message

/**
	* Created by kass on 03.11.16.
	*/
case class ConnectMessage(override val id: Long) extends Message {
}
