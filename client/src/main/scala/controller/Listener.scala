package controller

import java.nio.charset.Charset

import akka.actor.{Actor, Props}
import akka.io.Tcp.Connected
import akka.util.ByteString
import model.messaging.Message
import model.messaging.requests.{LoggedMessage, PaintingMessage, TextMessage}
import model.messaging.response._

import scala.pickling.Defaults._
import scala.pickling.json._

/**
	* Created by kass on 22.10.16.
	*/
object Listener {
	def props(): Props = Props(new Listener())
}

class Listener extends Actor {
	override def receive = {
		case x: ByteString =>

			val tmp = x.decodeString(Charset.defaultCharset())
			tmp.unpickle[Message] match {
				case a: TextMessage => Controller.showText(a)
				case a: PaintingMessage => PaintingController.paintMessage(a)
				case CheckMessage(id, check, "LOGIN") =>
					Controller.changeScreen(check)
					if (check) sender() ! ByteString(LoggedMessage(id).pickle.value)
				case CheckMessage(id, check, "REGISTER") =>
					if (check) Controller.showInfo("registration success")
					else Controller.showInfo("registration failed")
				case CheckMessage(id, check, action) => //
				case a: ConnectMessage => Controller.id = a.id
				case a: AvailableUsers => Controller.showUsers(a.getUsers)
				case a: TalkMessage => Controller.beginTalk(a)
				case a: OkMessage => //
				case b: Any => println("no such Send type error: " + b.getClass.toString)
			}
		case x: String => println(x)
		case c@Connected(remote, local) => //
		case b: Any => println("massage not a ByteString: " + b.getClass.toString)
	}
}
