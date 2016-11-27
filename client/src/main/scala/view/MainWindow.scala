package view

import controller.Controller
import model.messaging.requests.TextMessage

import scala.pickling.Defaults._
import scala.pickling.json._
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.canvas._
import scalafx.scene.control._
import scalafx.scene.control.cell.CheckBoxListCell
import scalafx.scene.layout.GridPane
import scalafx.scene.paint.Color

/**
	* Created by Katarzyna Herman on 08.10.16.
	*/
object MainWindow extends JFXApp.PrimaryStage {
	val mW = this
	height = 480
	width = 640
	val receivedText = new TextArea("otrzymane wiadomości") {
		editable = false
		prefHeight <== (mW.height - 10) * 0.6
		prefWidth <== (mW.width - 10) * 0.6
	}
	val writtenText = new TextField {
		text = "tu będzie później tekst"
		prefHeight <== (mW.height - 10) * 0.35
		prefWidth <== (mW.width - 10) * 0.6
		alignment = Pos.BottomLeft
	}
	val paintingArea = new Canvas {
		height <== (mW.height - 10) * 0.75
		width <== (mW.width - 10) * 0.4
	}
	val paintingButtons = new ButtonBar {
		buttons = List(
			new Button {
				text = ":)"
			},
			new Button("A"),
			new Button("1"),
			new Button("send") {
				onAction = handle {
					if (Controller.talking)
						Controller.send(
							TextMessage(Controller.login.id, writtenText.text.value, Controller.talkId)
								.pickle.value)
				}
			}
		)
	}
	val writingButtons = new ButtonBar {
		buttons = List(
			new Button {
				text = ":)"
			},
			new Button("A"),
			new Button("1"),
			new Button("%")
		)
	}
	val usersList = new ListView[UserCheck]() {
		cellFactory = CheckBoxListCell.forListView(_.selected)
	}
	/*val usersCheckboxesList = ObservableBuffer[UserCheck]()
	usersList.items = usersCheckboxesList*/

	scene = new Scene {
		root = new GridPane {
			hgap = 10 //mW.height * 0.1
			vgap = 10 //mW.width * 0.1
			padding = Insets(10)
			//			alignment = Pos.Center
			writingButtons.setMaxWidth((mW.getWidth - 10) * 0.4)
			writingButtons.setMaxHeight((mW.getHeight - 30) * 0.25)
			val gc = paintingArea.getGraphicsContext2D
			gc.setFill(Color.GhostWhite)
			gc.fillRect(0, 0, paintingArea.getWidth, paintingArea.getHeight)
			add(paintingButtons, 0, 1, 1, 1)
			add(writtenText, 1, 1, 1, 1)
			add(paintingArea, 0, 0, 1, 1)
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
					}
				}

			}, 2, 1, 1, 1)
		}
	}
}
