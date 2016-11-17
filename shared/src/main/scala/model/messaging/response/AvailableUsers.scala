package model.messaging.response

import model.messaging.Message

import scala.collection.mutable

/**
	* Created by Katarzyna Herman on 03.11.16.
	*/
case class AvailableUsers(override val id: Long, users: mutable.LinkedHashMap[Long, String]) extends Message {
	def getUsers: mutable.LinkedHashMap[Long, String] = users
}
