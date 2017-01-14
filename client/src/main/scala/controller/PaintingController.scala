package controller

import model.messaging.requests.PaintingMessage

import scala.collection.mutable
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.image.Image
import scalafx.scene.paint.Color

/**
	* Created by Katarzyna Herman on 03.12.16.
	*/
object PaintingController {
	var gc: GraphicsContext = _
	var fill: Color = Color.Black
	var stroke: Color = Color.Black
	var image: Image = _
	var points: mutable.MutableList[(Double, Double)] = new scala.collection.mutable.MutableList[(Double, Double)]

	def paintMessage(a: PaintingMessage): Unit = {
		gc.stroke = a.getFill
		gc.beginPath()
		val startPoint = a.getPoints.head
		PaintingController.gc.moveTo(startPoint._1, startPoint._2)
		a.getPoints.foreach(a => PaintingController.gc.lineTo(a._1, a._2))
		PaintingController.gc.strokePath()
	}
}
