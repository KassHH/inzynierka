package model.messaging.requests

import model.messaging.Message

/**
	* Created by Katarzyna Herman on 30.10.16.
	*/
case class CredentialsMessage(override val id: Long, username: String, password: String, var action: String) extends Message {

}
