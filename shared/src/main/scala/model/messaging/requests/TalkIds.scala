package model.messaging.requests

import model.messaging.Message

import scala.pickling.Defaults._
import scala.pickling.json._

/**
	* Created by Katarzyna Herman on 30.10.16.
	*/
case class TalkIds(override val id: Long, ids: String) extends Message {
	def getSet: Set[Long] = ids.unpickle[Set[Long]]
}
