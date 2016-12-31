package model.messaging.requests

import model.messaging.Message

import scala.pickling.Defaults._
import scala.pickling.json._
import scalafx.scene.paint.Color

/**
	* Created by Katarzyna Herman on 31.12.16.
	*/
case class PaintingMessage(override val id: Long, talkId: Long, points: String, fillR: Double, fillG: Double, fillB: Double) extends Message {
	def getPoints: List[(Double, Double)] = points.unpickle[List[(Double, Double)]]

	def getFill: Color = Color.rgb((255 * fillR).toInt, (fillG * 255).toInt, (fillB * 255).toInt)
}
