package controller

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef, DeadLetter, Props}
import akka.io.{IO, Tcp}
import akka.util.ByteString
import model._
import model.messaging.response.ConnectMessage

import scala.collection.mutable
import scala.pickling.Defaults._
import scala.pickling.json._

/**
	* Created by Katarzyna Herman on 28.10.16.
	*/
object Server {
	val connectionHandlers = new mutable.HashMap[Long, ActorRef]()
}

class Server extends Actor {

	import Tcp._
	import context.system

	IO(Tcp) ! Bind(self, new InetSocketAddress(Properties.PATH, Properties.PORT))

	def receive = {
		case b@Bound(localAddress) =>
		case CommandFailed(_: Bind) => context stop self
		case d: DeadLetter => println(d.message)
		case c@Connected(remote, local) =>
			val handler = context.actorOf(Props[Handler])
			val connection = sender()
			Server.connectionHandlers += ((ServerController.count, handler))
			connection ! Register(handler)
			connection ! Write(ByteString(ConnectMessage(ServerController.count.toLong).pickle.value))
			ServerController.count += 1
	}
}


