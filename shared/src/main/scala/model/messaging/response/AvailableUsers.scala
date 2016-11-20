package model.messaging.response

import model.messaging.Message

import scala.collection.immutable
import scala.pickling.Defaults._
import scala.pickling.json._


/**
	* Created by Katarzyna Herman on 03.11.16.
	*/
case class AvailableUsers(override val id: Long, users: String) extends Message {
	def getUsers: Map[Long, String] = users.unpickle[immutable.Map[Long, String]]
}
