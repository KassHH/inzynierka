package controller

import java.io.{BufferedWriter, File, FileWriter}

import akka.util.ByteString
import model.messaging.Message
import model.messaging.requests._
import model.messaging.response.{AvailableUsers, CheckMessage, OkMessage, TalkMessage}

import scala.collection.mutable
import scala.io.Source
import scala.pickling.Defaults._
import scala.pickling.json._

/**
	* Created by kass on 04.11.16.
	*/
object ServerController {
	val filename = "registeredUsers.txt"
	var connectedUsers = new mutable.HashMap[Long, String]()
	var talks = new mutable.HashMap[Long, Set[Long]]
	var talkCount = 0
	var count = 0
	var users = new mutable.HashMap[String, String]

	if (new File(filename).exists()) {
		val fileContents = Source.fromFile(filename).getLines().mkString
		val registeredUsers = fileContents.unpickle[Map[String, String]]
		users ++= registeredUsers
	} else {
		users += (("user", "pass"), ("user2", "123"))
	}

	def giveReply(request: String): ByteString = {
		var reply: String = ""
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
				} else reply = CheckMessage(id, check = false, "PASS_CHANGE").pickle.value

			case CredentialsMessage(id, username, password, "REGISTER") =>
				if (!users.contains(username)) {
					users += ((username, password))
					ServerController.saveRegistered()
					reply = CheckMessage(id, check = true, "REGISTER").pickle.value
				} else reply = CheckMessage(id, check = false, "REGISTER").pickle.value


			case a: CheckMessage => //
			case a: LoggedMessage =>
				val s = connectedUsers.keySet
				val b = connectedUsers.toMap.pickle.value
				val rep = AvailableUsers(a.getId, b)
				reply = rep.pickle.value
				val tlk = s.collect(Server.connectionHandlers)
				val tmp = tlk.-(Server.connectionHandlers(a.id))
				tmp.foreach(c => c.tell(rep, Server.connectionHandlers(a.id)))

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

	def saveRegistered() = {
		val output = new File(filename)
		if (output.exists()) {
			output.delete()
		}
		output.createNewFile()
		val bw = new BufferedWriter(new FileWriter(output))
		bw.write(users.toMap.pickle.value)
		bw.close()
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
