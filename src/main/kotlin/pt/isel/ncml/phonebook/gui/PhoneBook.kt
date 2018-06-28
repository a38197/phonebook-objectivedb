package pt.isel.ncml.phonebook.gui

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage

class PhoneBook : Application() {

    override fun start(primaryStage: Stage) {
        val loader = FXMLLoader(javaClass.getResource("/fxml/main.fxml"))
        val root = loader.load<AnchorPane>()
        val scene = Scene(root)
        primaryStage.scene = scene
        primaryStage.show()
    }

}
