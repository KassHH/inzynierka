package model.messaging.response

import model.messaging.Message

import scala.pickling.Defaults._
import scala.pickling.json._

/**
	* Created by kass on 03.11.16.
	*/
case class TalkMessage(override val id: Long, ids: String) extends Message {
	def getSet: Set[Long] = ids.unpickle[Set[Long]]
}
