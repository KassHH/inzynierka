package controller

import akka.util.ByteString
import model.messaging.Message
import model.messaging.requests.{CredentialsMessage, Logged, TextMessage}
import model.messaging.response.{AvailableUsers, CheckMessage}

import scala.collection.mutable
import scala.pickling.Defaults._
import scala.pickling.json._

/**
	* Created by kass on 04.11.16.
	*/
object ServerController {

	var connectedUsers = new mutable.HashMap[Long, String]()
	var count = 0
	var users = new mutable.HashMap[String, String]
	users += (("user", "pass"), ("user2", "123"))

	//	connectedUsers += ((123,"test1"),(321,"test2"))
	def giveReply(request: String): ByteString = {
		var reply: String = ""
		println(request)
		request.unpickle[Message] match {
			case a: TextMessage => print(a.getText)
			case a: CredentialsMessage =>
				reply = credentialsActionCheck(a).pickle.value
			case a: CheckMessage => print(a.getCheck)
			case a: Logged =>
				val s = connectedUsers.toMap.pickle.value //s.
				reply = AvailableUsers(a.getId, s).pickle.value
		}
		ByteString(reply)
	}

	def credentialsActionCheck(c: CredentialsMessage): CheckMessage = {
		c.getActionType match {
			case "LOGIN" => checkCredentials(c)
			case "PASS_CHANGE" => if (users.contains(c.getUserName)) {
				users.update(c.getUserName, c.getPassword)
			} else {
				return CheckMessage(c.getId, check = false)
			}
			case "DELETE" => users -= c.getUserName
			case "REGISTER" => if (!users.contains(c.getUserName)) {
				users += ((c.getUserName, c.getPassword))
			} else {
				return CheckMessage(c.getId, check = false)
			}
			case _ => return CheckMessage(c.getId, check = false)
		}

		CheckMessage(c.getId, check = true)
	}

	def checkCredentials(credentials: CredentialsMessage): CheckMessage = {
		if (users.contains(credentials.getUserName)) {
			if (credentials.getPassword == users(credentials.getUserName)) {
				connectedUsers += ((credentials.getId, credentials.getUserName))
				return CheckMessage(credentials.id, check = true)
			}
		}
		CheckMessage(credentials.id, check = false)
	}
}
