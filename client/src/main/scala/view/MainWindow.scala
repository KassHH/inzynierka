package view

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.canvas._
import scalafx.scene.control._
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
	val usersList = new ListView[CheckBox]()
	val usersCheckboxesList = ObservableBuffer[CheckBox]()
	usersList.items = usersCheckboxesList

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
		}
	}
}
