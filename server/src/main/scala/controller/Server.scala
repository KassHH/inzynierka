package controller

import java.net.InetSocketAddress
import java.nio.charset.Charset

import akka.actor.{Actor, ActorRef, DeadLetter, Props}
import akka.io.{IO, Tcp}
import akka.util.ByteString
import model._
import model.messaging.response.ConnectMessage

import scala.collection.mutable
import scala.pickling.Defaults._
import scala.pickling.json._

/**
	* Created by kass on 28.10.16.
	*/
object Server {
	val connections = new mutable.LinkedHashMap[Long, ActorRef]()
}

class Server extends Actor {

	import Tcp._
	import context.system

	IO(Tcp) ! Bind(self, new InetSocketAddress("localhost", Properities.PORT))

	def receive = {
		case b@Bound(localAddress) =>
		// do some logging or setup ...
		case CommandFailed(_: Bind) => context stop self
		case d: DeadLetter => println(d)
		case c@Connected(remote, local) =>
			println("connected with" + c)
			val handler = context.actorOf(Props[SimplisticHandler])
			Server.connections += ((ServerController.count, handler))
			val connection = sender()
			connection ! Register(handler)
			connection ! Write(ByteString(ConnectMessage(ServerController.count.toLong).pickle.value))
			ServerController.count += 1
		//			Server.connections.foreach(_._2 ! "got")
	}

}

class SimplisticHandler extends Actor {

	import Tcp._

	val replyTo = sender()

	def receive = {
		case Received(data) =>
			val stringData = data.decodeString(Charset.defaultCharset())
			val byteData = ServerController.giveReply(stringData)
			replyTo ! Write(byteData)
		case d: DeadLetter => println(d)
		case PeerClosed => context stop self
		case _ => println("tu! ")
	}
}

