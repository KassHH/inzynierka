package controller

import java.nio.charset.Charset

import akka.actor.{Actor, ActorRef, DeadLetter}
import akka.io.Tcp
import akka.util.ByteString
import model.messaging.requests.{PaintingMessage, TextMessage}
import model.messaging.response.{AvailableUsers, TalkMessage}

import scala.pickling.Defaults._
import scala.pickling.json._


/**
	* Created by Katarzyna Herman on 26.11.16.
	*/

class Handler extends Actor {

	import Tcp._

	var myActor: ActorRef = _
	var id: Long = _
	def receive = {
		case Received(data) =>
			myActor = sender()
			val stringData = data.decodeString(Charset.defaultCharset())
			val byteData = ServerController.giveReply(stringData)
			id = stringData.unpickle[model.messaging.Message].id
			sender() ! Write(byteData)
		case d: DeadLetter => println(d.message)
		case a: TextMessage =>
			myActor ! Write(ByteString(a.pickle.value))
		case a: PaintingMessage =>
			myActor ! Write(ByteString(a.pickle.value))
		case a: TalkMessage =>
			myActor ! Write(ByteString(a.pickle.value))
		case PeerClosed =>
			ServerController.connectedUsers -= id
			context stop self
		case a: AvailableUsers =>
			val b = AvailableUsers(a.id, a.users).pickle.value
			myActor ! Write(ByteString(b))
		case a: model.messaging.Message =>
			myActor ! Write(ByteString(a.pickle.value))
		case a: ByteString => println("ByteString: " + a.decodeString(Charset.defaultCharset()))
		case b: Any => println(b.getClass.toString)
	}
}

