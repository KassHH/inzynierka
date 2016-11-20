package controller

import java.net.InetSocketAddress

import akka.actor._
import akka.util.ByteString
import model._
import model.messaging.requests.CredentialsMessage
import view.{LoginWindow, MainWindow}

import scalafx.application.{JFXApp, Platform}

/**
	* Created by kass on 15.10.16.
	*/
object Controller extends JFXApp {


	stage = LoginWindow

	val remote = new InetSocketAddress("localhost", Properities.PORT)
	val system = ActorSystem("mySystem")
	val listener = system.actorOf(Listner.props(), "handler")
	val connectionActor = system.actorOf(Client.props(remote, listener), "client")
	var login: CredentialsMessage = _
	var id: Long = _

	override def stopApp(): Unit = {
		super.stopApp()
		System.exit(0)
	}

	def addCredentials(userName: String, pass: String, actionType: String)
	= login = CredentialsMessage(id, userName, pass, actionType)

	def send(msg: String) = {
		connectionActor.tell(ByteString(msg), listener)
	}

	def changeScreen(check: Boolean) = {
		Platform.runLater(new Runnable {
			override def run(): Unit = {
				if (check) {
					MainWindow.show()
				}
				else {
					//stage.
					LoginWindow.alertLabel.visible = true
				}
			}
		})
	}

	def showUsers(users: Map[Long, String]): Unit = {
		println(users.aggregate("\n")(_ + _._2, _ + _))
		//stage
		MainWindow.receivedText.text = users.aggregate("\n")(_ + _._2, _ + " " + _)
	}


	def startTalk(ids: Set[Long]) = ???
}