package controller

import java.nio.charset.Charset

import akka.actor.{Actor, Props}
import akka.io.Tcp.Connected
import akka.util.ByteString
import model.messaging.Message
import model.messaging.requests.{Logged, TextMessage}
import model.messaging.response.{AvailableUsers, CheckMessage, ConnectMessage, TalkMessage}

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
			x.decodeString(Charset.defaultCharset()).unpickle[Message] match {
				case a: TextMessage => Controller.showText(a)
				case a: CheckMessage => {
					Controller.changeScreen(a.check)
					sender() ! ByteString(Logged(a.id).pickle.value)
				}
				case a: ConnectMessage => Controller.id = a.id
				case a: AvailableUsers => Controller.showUsers(a.getUsers)
				case a: TalkMessage => Controller.beginTalk(a)
				case b: Any => println("another error (no such Send type) " + b.toString)
			}
		case x: String => println("dostałem Stringa" + x)
		case c@Connected(remote, local) => println("ok! Connected!")
		case b: Any => println("some error(massage not a string) " + b.toString)
	}
}
