package controller

import java.nio.charset.Charset

import akka.actor.{Actor, ActorRef, DeadLetter}
import akka.io.Tcp
import akka.util.ByteString
import model.messaging.requests.{PaintingMessage, TextMessage}
import model.messaging.response.TalkMessage

import scala.pickling.Defaults._
import scala.pickling.json._


/**
	* Created by Katarzyna Herman on 26.11.16.
	*/

class Handler extends Actor {

	import Tcp._

	var myActor: ActorRef = _

	//println(sender().path.name)
	def receive = {
		case Received(data) =>
			myActor = sender()
			val stringData = data.decodeString(Charset.defaultCharset())
			println(sender() + " ||| " + sender().path)
			val byteData = ServerController.giveReply(stringData)
			sender() ! Write(byteData)
		case d: DeadLetter => println(d)
		case a: TextMessage =>
			myActor ! Write(ByteString(a.pickle.value))
		case a: PaintingMessage =>
			myActor ! Write(ByteString(a.pickle.value))
		case a: TalkMessage =>
			myActor ! Write(ByteString(a.pickle.value))
		case PeerClosed => context stop self
		case a: ByteString => println("tu! " + a.decodeString(Charset.defaultCharset()))
		case b: Any => println("nie wiadomo co przyszło: " + b)
	}
}

