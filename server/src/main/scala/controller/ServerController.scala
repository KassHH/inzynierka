package controller

import akka.util.ByteString
import model.messaging.Message
import model.messaging.requests._
import model.messaging.response.{AvailableUsers, CheckMessage, OkMessage, TalkMessage}

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
	users += (("user", "pass"), ("user2", "123"), ("user3", "pass"), ("user4", "pass"))

	//	connectedUsers += ((123,"test1"),(321,"test2"))
	def giveReply(request: String): ByteString = {
		var reply: String = ""
		println(request)
		request.unpickle[Message] match {
			case a: TextMessage =>
				val tlk = talks(a.talkId).collect(Server.connectionHandlers)
				reply = OkMessage(a.id).pickle.value
				tlk.foreach(c => c.tell(a, Server.connectionHandlers(a.id)))
			case a: PaintingMessage =>
				val tlk = talks(a.talkId).collect(Server.connectionHandlers)
				reply = OkMessage(a.id).pickle.value
				tlk.foreach(c => c.tell(a, Server.connectionHandlers(a.id)))

			case CredentialsMessage(id, username, password, "LOGIN") =>
				reply = checkCredentials(password, id, username).pickle.value
			case CredentialsMessage(id, username, password, "DELETE") =>
				users -= username
				reply = CheckMessage(id, check = true, "DELETE").pickle.value
			case CredentialsMessage(id, username, password, "PASS_CHANGE") =>
				if (users.contains(username)) {
					users.update(username, password)
					reply = CheckMessage(id, check = true, "PASS_CHANGE").pickle.value
				} else {
					reply = CheckMessage(id, check = false, "PASS_CHANGE").pickle.value
				}
			case CredentialsMessage(id, username, password, "REGISTER") =>
				if (!users.contains(username)) {
					users += ((username, password))
					reply = CheckMessage(id, check = true, "REGISTER").pickle.value
				} else {
					reply = CheckMessage(id, check = false, "REGISTER").pickle.value
				}

			case a: CheckMessage => print(a.check)
			case a: LoggedMessage =>
				val s = connectedUsers.toMap.pickle.value //s.
				reply = AvailableUsers(a.getId, s).pickle.value
			case a: TalkIdsMessage =>
				val b = a.getSet.collect(Server.connectionHandlers)
				val set: mutable.HashSet[Long] = mutable.HashSet() ++ a.getSet
				set += a.id
				talks += ((talkCount, set.toSet))
				val s = TalkMessage(talkCount, a.ids)
				reply = s.pickle.value
				b.foreach(c => c.tell(s, Server.connectionHandlers(a.id)))
				talkCount += 1
		}
		ByteString(reply)
	}

	def checkCredentials(password: String, id: Long, username: String): CheckMessage = {
		if (users.contains(username)) {
			if (password == users(username)) {
				connectedUsers += ((id, username))
				return CheckMessage(id, check = true, "LOGIN")
			}
		}
		CheckMessage(id, check = false, "LOGIN")
	}
}
