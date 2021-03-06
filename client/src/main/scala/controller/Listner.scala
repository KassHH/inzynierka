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
object Listner {
	def props(): Props = Props(new Listner())
}
class Listner extends Actor {
	override def receive = {
		case x: ByteString =>
			//	println(x.decodeString(Charset.defaultCharset()))
			val tmp = x.decodeString(Charset.defaultCharset())

			tmp.unpickle[Message] match {
				case a: TextMessage => Controller.showText(a)
				case a: PaintingMessage => PaintingController.paintMessage(a)
				case a: CheckMessage => {
					Controller.changeScreen(a.check)
					if (a.check) {
					sender() ! ByteString(LoggedMessage(a.id).pickle.value)
					}
				}
				case a: ConnectMessage => Controller.id = a.id
				case a: AvailableUsers => Controller.showUsers(a.getUsers)
				case a: TalkMessage => Controller.beginTalk(a)
				case a: OkMessage => //
				case b: Any => println("another error (no such Send type) " + b.toString)
			}
		case x: String => println("dostałem Stringa" + x)
		case c@Connected(remote, local) => println("ok! Connected!")
		case b: Any => println("some error(massage not a string) " + b.toString)
	}
}
