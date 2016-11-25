package controller

import java.net.InetSocketAddress

import akka.actor._
import akka.util.ByteString
import model._
import model.messaging.requests.CredentialsMessage
import view.{LoginWindow, MainWindow, UserCheck}

import scala.collection.immutable.HashMap
import scalafx.application.{JFXApp, Platform}
import scalafx.collections.ObservableBuffer

/**
	* Created by kass on 15.10.16.
	*/
object Controller extends JFXApp {


	stage = LoginWindow
	val remote = new InetSocketAddress("localhost", Properities.PORT)
	val system = ActorSystem("mySystem")
	val listener = system.actorOf(Listner.props(), "handler")
	val connectionActor = system.actorOf(Client.props(remote, listener), "client")
	var talkingWith = new HashMap[String, Long]
	var login: CredentialsMessage = _
	var id: Long = _
	var usersCheckboxesList: ObservableBuffer[UserCheck] = _
	var talking: Boolean = false

	override def stopApp(): Unit = {
		super.stopApp()
		System.exit(0)
	}

	def addCredentials(userName: String, pass: String, actionType: String)
	= login = CredentialsMessage(id, userName, pass, actionType)

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
		usersCheckboxesList = ObservableBuffer[UserCheck](users map { case (id, name) => new UserCheck(name = name, id = id) } toBuffer).filter(a => a.getId != id)
		//val usersCheckboxesList = ObservableBuffer[CheckBox](users map { case (id, name) => new CheckBox(name) } toSeq)
		MainWindow.usersList.items = usersCheckboxesList

		//stage
		//MainWindow.receivedText.text = users.aggregate("\n")(_ + _._2, _ + " " + _)
	}

	def startTalk() = {
		val id = usersCheckboxesList.map(d => if (d.selected.value) d.getId).toSet
		send("ids")
	}

	def send(msg: String) = {
		connectionActor.tell(ByteString(msg), listener)
	}
}