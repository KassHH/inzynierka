package view

import controller.Controller

import scala.pickling.Defaults._
import scala.pickling.json._
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, TextField}
import scalafx.scene.layout.{HBox, VBox}


object LoginWindow extends JFXApp.PrimaryStage {
	title = "communicator"
	val login = new TextField()
	val password = new TextField()
	val alertLabel = new Label(
		text = "zły login lub hasło"
	)
	alertLabel.visible = false
	scene = new Scene {
		root = new VBox {
			spacing = 10
			alignment = Pos.Center
			padding = Insets(25)
			//val btn = new Button()
			children = Seq(
				new Label(
					text = "login"
				),
				login,
				new Label(
					text = "password"
				),
				password,
				alertLabel, new HBox() {
					children = Seq(new Button(
						text = "log in"
					) {
						onAction = handle {
							Controller.addCredentials(login.getText, password.getText, "LOGIN")
							Controller.send(Controller.login.pickle.value)
							//MainWindow.show()
						}
					},
						new Button(
							text = "register"
						) {
							onAction = handle {
								Controller.addCredentials(login.getText, password.getText, "REGISTER")
								Controller.send(Controller.login.pickle.value)
								//MainWindow.show()
							}
						}
					)
				}
			)
		}
	}
}