import java.net.InetSocketAddress

import akka.actor.{ActorSystem, DeadLetter, Props}
import controller._
import model.Properities
import model.messaging.requests.CredentialsMessage

object ServerMain extends App {
	val remote = new InetSocketAddress("localhost", Properities.PORT)
	val system = ActorSystem("serverSystem")
	val connectionActor = system.actorOf(Props[Server], "server")
	var login: CredentialsMessage = _
	system.eventStream.subscribe(connectionActor, classOf[DeadLetter])

}