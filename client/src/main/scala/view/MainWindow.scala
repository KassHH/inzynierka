package view

import javax.imageio.ImageIO

import controller.{Controller, PaintingController}
import model.messaging.requests.{PaintingMessage, TextMessage}

import scala.pickling.Defaults._
import scala.pickling.json._
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.embed.swing.SwingFXUtils
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.canvas._
import scalafx.scene.control._
import scalafx.scene.control.cell.CheckBoxListCell
import scalafx.scene.image.{Image, WritableImage}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter

/**
	* Created by Katarzyna Herman on 08.10.16.
	*/
object MainWindow extends JFXApp.PrimaryStage {

	val mW = this
	height = 480
	width = 640
	val receivedText = new TextArea("") {
		editable = false
		prefHeight <== (mW.height - 10) * 0.6
		prefWidth <== (mW.width - 10) * 0.6
	}
	val writtenText = new TextField {
		text = ""
		prefHeight <== (mW.height - 10) * 0.3
		prefWidth <== (mW.width - 10) * 0.6
		alignment = Pos.BottomLeft
	}
	val stack = new StackPane()
	val paintingArea = new Canvas() {
		height <== stack.height
		width <== stack.width
	}

	PaintingController.gc = paintingArea.graphicsContext2D
	stack.background = new Background(Array(new BackgroundFill(Color.White, CornerRadii.Empty, Insets.Empty)))
	stack.children.add(paintingArea)

	val paintingButtons = new ButtonBar {
		buttons = List(
			new Button {
				text = "save file"
				onAction = handle {
					val chooser = new FileChooser()
					chooser.title = "choose image"
					chooser.extensionFilters.addAll(
						new ExtensionFilter("JPG files (*.jpg)", Seq("*.JPG", "*.jpg", "*.jpeg", "*.JPEG")),
						new ExtensionFilter("PNG files (*.png)", "*.PNG"),
						new ExtensionFilter("GIF files (*.gif)", "*.GIF"))
					val file = chooser.showSaveDialog(null)

					val extension = file.getName.split('.')(1)
					val a = new WritableImage(paintingArea.width.value.toInt, paintingArea.height.value.toInt)
					paintingArea.snapshot(null, a)
					ImageIO.write(SwingFXUtils.fromFXImage(a, null), extension, file)
				}
			},
			new Button("open file") {
				onAction = handle {
					val chooser = new FileChooser()
					chooser.title = "choose image"
					chooser.extensionFilters.addAll(
						new ExtensionFilter("JPG files (*.jpg)", Seq("*.JPG", "*.jpg", "*.jpeg", "*.JPEG")),
						new ExtensionFilter("PNG files (*.png)", "*.PNG"),
						new ExtensionFilter("GIF files (*.gif)", "*.GIF"))
					val file = chooser.showOpenDialog(null)
					if (file != null) {
						image = new Image("file:" + file.getAbsolutePath)
						PaintingController.gc.drawImage(image, paintingArea.layoutX.value,
							paintingArea.layoutY.value, paintingArea.width.value,
							paintingArea.height.value)
					}
				}
			},
			new Button("clean") {
				onAction = handle {
					PaintingController.gc.fill = Color.White
					PaintingController.gc.fillRect(0, 0, paintingArea.getWidth, paintingArea.getHeight)
				}
			},
			new Button("send\ntext") {
				onAction = handle {
					if (Controller.talking)
						Controller.send(
							TextMessage(Controller.login.id, writtenText.text.value, Controller.talkId)
								.pickle.value)
					writtenText.text = ""
				}
			}
		)
	}

	//eventy do malowania
	paintingArea.onMousePressed = (me: MouseEvent) => {
		PaintingController.gc.beginPath()
		PaintingController.gc.stroke = PaintingController.stroke
		PaintingController.gc.fill = PaintingController.fill
		PaintingController.gc.moveTo(me.sceneX - 10, me.sceneY - 10)
		PaintingController.points.clear()
		PaintingController.points += ((me.sceneX - 10, me.sceneY - 10))
	}
	paintingArea.onMouseDragged = (me: MouseEvent) => {
		PaintingController.gc.lineTo(me.x, me.y)
		PaintingController.points += ((me.x, me.y))
	}
	paintingArea.onMouseReleased = (me: MouseEvent) => {
		PaintingController.gc.strokePath()
		val color: Color = PaintingController.stroke
		if (Controller.talking) {
		Controller.send(PaintingMessage(
			Controller.id, Controller.talkId,
			PaintingController.points.toList.pickle.value,
			color.red, color.green, color.blue).pickle.value)
	}
	}

	val usersList = new ListView[UserCheck]() {
		cellFactory = CheckBoxListCell.forListView(_.selected)
	}
	var image: Image = _

	scene = new Scene {
		root = new GridPane {
			hgap = 10
			vgap = 10
			padding = Insets(10)
			//	PaintingController.gc.fill = Color.White
			//	PaintingController.gc.fillRect(0, 0, paintingArea.getWidth, paintingArea.getHeight)
			add(paintingButtons, 0, 1, 1, 1)
			add(writtenText, 1, 1, 1, 1)
			add(stack, 0, 0, 1, 1)
			add(receivedText, 1, 0, 1, 1)
			add(usersList, 2, 0, 1, 1)
			add(new Button("talk") {
				onAction = handle {
					if (!Controller.talking) {
						Controller.startTalk()
						Controller.talking = true
						text = "end talk"
						usersList.visible = false
					} else {
						Controller.talking = false
						text = "talk"
						usersList.visible = true
						//remove from talk message
					}
				}
			}, 2, 1, 1, 1)
		}
	}
}
