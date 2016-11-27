package controller

import akka.util.ByteString
import model.messaging.Message
import model.messaging.requests.{CredentialsMessage, Logged, TalkIds, TextMessage}
import model.messaging.response.{AvailableUsers, CheckMessage, TalkMessage}

import scala.collection.mutable
import scala.pickling.Defaults._
import scala.pickling.json._

/**
	* Created by kass on 04.11.16.
	*/
object ServerController {

	var connectedUsers = new mutable.HashMap[Long, String]()
	var talks = new mutable.HashMap[Long, Set[Long]]
	var talkCount = 0
	var count = 0
	var users = new mutable.HashMap[String, String]
	users += (("user", "pass"), ("user2", "123"))

	//	connectedUsers += ((123,"test1"),(321,"test2"))
	def giveReply(request: String): ByteString = {
		var reply: String = ""
		println(request)
		request.unpickle[Message] match {
			case a: TextMessage =>
				val tlk = //talks(a.talkId).collect
					(Server.connections)
				reply = a.pickle.value
				tlk.foreach(c => Server.connectionHandlers(c._1).tell(a, Server.connectionHandlers(a.id)))

			case a: CredentialsMessage =>
				reply = credentialsActionCheck(a).pickle.value
			case a: CheckMessage => print(a.check)
			case a: Logged =>
				val s = connectedUsers.toMap.pickle.value //s.
				reply = AvailableUsers(a.getId, s).pickle.value
			case a: TalkIds =>
				val b = //a.getSet.collect
					(Server.connections)
				talks += ((talkCount, a.getSet))
				val s = TalkMessage(talkCount, a.ids)
				reply = s.pickle.value
				b.foreach(c => Server.connectionHandlers(c._1).tell(s, Server.connectionHandlers(a.id)))
				talkCount += 1
		}
		ByteString(reply)
	}

	def credentialsActionCheck(c: CredentialsMessage): CheckMessage = {
		c.action match {
			case "LOGIN" => checkCredentials(c)
			case "PASS_CHANGE" => if (users.contains(c.username)) {
				users.update(c.username, c.password)
			} else {
				return CheckMessage(c.id, check = false)
			}
			case "DELETE" => users -= c.username
			case "REGISTER" => if (!users.contains(c.username)) {
				users += ((c.username, c.password))
			} else {
				return CheckMessage(c.id, check = false)
			}
			case _ => return CheckMessage(c.id, check = false)
		}

		CheckMessage(c.id, check = true)
	}

	def checkCredentials(credentials: CredentialsMessage): CheckMessage = {
		if (users.contains(credentials.username)) {
			if (credentials.password == users(credentials.username)) {
				connectedUsers += ((credentials.id, credentials.username))
				return CheckMessage(credentials.id, check = true)
			}
		}
		CheckMessage(credentials.id, check = false)
	}
}
