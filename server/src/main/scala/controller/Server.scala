package controller

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef, ActorSelection, DeadLetter, Props}
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
	// connections - map of id's of clients and ActorRefs for it's handlers
	val connectionHandlers = new mutable.HashMap[Long, ActorRef]()
	val connections = new mutable.HashMap[Long, ActorSelection]()
}

class Server extends Actor {

	import Tcp._
	import context.system

	IO(Tcp) ! Bind(self, new InetSocketAddress(Properities.PATH, Properities.PORT))

	def receive = {
		case b@Bound(localAddress) =>
		// do some logging or setup ...
		case CommandFailed(_: Bind) => context stop self
		case d: DeadLetter => println(d)
		case c@Connected(remote, local) =>
			println("connected with" + c)

			val handler = context.actorOf(Props[Handler])
			val connection = sender()
			Server.connectionHandlers += ((ServerController.count, handler))
			Server.connections += ((ServerController.count, context.actorSelection(connection.path)))
			connection ! Register(handler)
			connection ! Write(ByteString(ConnectMessage(ServerController.count.toLong).pickle.value))

			ServerController.count += 1
		//			Server.connections.foreach(_._2 ! "got")
	}

}


