package view

import controller.Controller

import scala.pickling.Defaults._
import scala.pickling.json._
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, PasswordField, TextField}
import scalafx.scene.layout.{HBox, VBox}


object LoginWindow extends JFXApp.PrimaryStage {
	title = "communicator"
	val login = new TextField()
	login.promptText = "Your username"
	val password = new PasswordField()
	password.promptText = "Your password"
	val alertLabel = new Label(
		text = ""
	)
	scene = new Scene {
		root = new VBox {
			spacing = 10
			alignment = Pos.Center
			padding = Insets(25)
			children = Seq(
				new Label(
					text = "login"
				),
				login,
				new Label(
					text = "password"
				),
				password,
				alertLabel,
				new HBox() {
					children = Seq(
						new Button(
							text = "log in"
						) {
							onAction = handle {
								Controller.addCredentials(login.text.value, password.text.value, "LOGIN")
								Controller.send(Controller.login.pickle.value)
								login.text = ""
								password.text = ""
							}
						},
						new Button(
							text = "register"
						) {
							onAction = handle {
								Controller.addCredentials(login.text.value, password.text.value, "REGISTER")
								Controller.send(Controller.login.pickle.value)
								login.text = ""
								password.text = ""
							}
						}
					)
				}
			)
		}
	}
}