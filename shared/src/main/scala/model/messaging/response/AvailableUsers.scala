package model.messaging.response

import model.messaging.Message

import scala.collection.mutable
import scala.pickling.Defaults._
import scala.pickling.json._


/**
	* Created by Katarzyna Herman on 03.11.16.
	*/
case class AvailableUsers(override val id: Long, users: String) extends Message {
	def getUsers: mutable.HashMap[Long, String] = users.unpickle[mutable.HashMap[Long, String]]
}
