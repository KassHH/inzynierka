package controller

import java.net.InetSocketAddress

import akka.actor._
import akka.util.ByteString
import model._
import model.messaging.requests.{CredentialsMessage, TalkIdsMessage, TextMessage}
import model.messaging.response.TalkMessage
import view.{LoginWindow, MainWindow, UserCheck}

import scala.pickling.Defaults._
import scala.pickling.json._
import scalafx.application.{JFXApp, Platform}
import scalafx.collections.ObservableBuffer

/**
	* Created by kass on 15.10.16.
	*/
object Controller extends JFXApp {


	stage = LoginWindow
	val remote = new InetSocketAddress(Properties.PATH, Properties.PORT)
	val system = ActorSystem("mySystem")
	val listener = system.actorOf(Listner.props(), "handler")
	val connectionActor = system.actorOf(Client.props(remote, listener), "client")
	var talkingWith: Map[Long, String] = _
	var login: CredentialsMessage = _
	var id: Long = _
	var usersCheckboxesList: ObservableBuffer[UserCheck] = new ObservableBuffer[UserCheck]()
	var talking: Boolean = false
	var talkId: Long = _

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
				} else {
					LoginWindow.alertLabel.visible = true
				}
			}
		})
	}

	def showUsers(users: Map[Long, String]): Unit = {
		usersCheckboxesList.removeAll(usersCheckboxesList)
		usersCheckboxesList = ObservableBuffer[UserCheck](users map { case (id, name) => new UserCheck(name = name, id = id) } toBuffer).filter(a => a.getId != id)
		MainWindow.usersList.items = usersCheckboxesList
	}

	def startTalk() = {
		val ids: Set[Long] = usersCheckboxesList.filter(d => d.selected.value).map(d => d.getId).toSet
		talkingWith = usersCheckboxesList.filter(d => d.selected.value).map(d => (d.getId, d.getName)).toMap
		val a = TalkIdsMessage(login.getId, ids.pickle.value)
		send(a.pickle.value)
	}

	def send(msg: String) = {
		connectionActor.tell(ByteString(msg), listener)
	}

	def showText(tM: TextMessage) = {
		val textBuffer = MainWindow.receivedText.text.value
		MainWindow.receivedText.text = textBuffer + tM.id + ": " + tM.text + "\n"
	}

	def beginTalk(a: TalkMessage): Unit = {
		Controller.talking = true
		//todo with who you talk?
		Controller.talkId = a.id
		MainWindow.usersList.visible = false
	}
}