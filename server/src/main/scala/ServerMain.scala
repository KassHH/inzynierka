import java.net.InetSocketAddress

import akka.actor.{ActorSystem, DeadLetter, Props}
import controller._
import model.Properties
import model.messaging.requests.CredentialsMessage

import scalafx.application.JFXApp
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.Label
import scalafx.scene.layout.HBox

object ServerMain extends JFXApp {
	val remote = new InetSocketAddress("localhost", Properties.PORT)
	val system = ActorSystem("serverSystem")
	val connectionActor = system.actorOf(Props[Server], "server")
	val labelText = "Server lunched"
	system.eventStream.subscribe(connectionActor, classOf[DeadLetter])
	var login: CredentialsMessage = _

	override def stopApp(): Unit = {
		super.stopApp()
		System.exit(0)
	}

	stage = new JFXApp.PrimaryStage {
		title = "server"
		scene = new Scene {
			root = new HBox {
				spacing = 10
				alignment = Pos.Center
				padding = Insets(25)
				children = Seq(new Label() {
					text = labelText
				})
			}
		}
	}

}