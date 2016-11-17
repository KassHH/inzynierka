package controller

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef, Props}
import akka.io.{IO, Tcp}
import akka.util.ByteString

/**
	* Created by kass on 23.10.16.
	*/
object Client {
	def props(remote: InetSocketAddress, replies: ActorRef) =
		Props(classOf[Client], remote, replies)
}

class Client(remote: InetSocketAddress, listener: ActorRef) extends Actor {

	import Tcp._
	import context.system

	val manager = IO(Tcp)
	manager ! Connect(remote)

	def receive = {
		case CommandFailed(_: Connect) =>
			listener ! "connect failed"
			context stop self

		case c@Connected(remote, local) =>
			listener ! c
			val connection = sender()
			connection ! Register(self)
			context become {
				case data: ByteString =>
					connection ! Write(data)
				case CommandFailed(w: Write) =>
				// O/S buffer was full
				case Received(data) =>
					listener ! data
				case "close" =>
					connection ! Close
				case _: ConnectionClosed =>
					listener ! "connection closed"
					context stop self
				case a: Any => println(a.toString)
			}
	}
}

