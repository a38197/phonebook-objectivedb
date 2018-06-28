package pt.isel.ncml.phonebook.gui

import javafx.event.ActionEvent
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Modality
import javafx.stage.Stage
import java.net.URL

object FX {

    fun <ROOT : Parent, C> launchModal(parent: Scene, resource:URL, title: String, controllerInit: (C)->Unit) : C {
        val loader = FXMLLoader(resource)
        val root = loader.load<ROOT>()
        val controller = loader.getController<C>()

        controllerInit(controller)
        configWindowModal(parent, root, title)
        return controller
    }

    fun <ROOT : Parent, C> launch(parent: Scene, resource:URL, title: String, controllerInit: (C)->Unit) : C {
        val loader = FXMLLoader(resource)
        val root = loader.load<ROOT>()
        val controller = loader.getController<C>()

        controllerInit(controller)
        configWindow(root, title)
        return controller
    }

    private fun configWindowModal(parent: Scene, root: Parent, title:String) {
        val stage = Stage()
        with(stage){
            this.title = title
            this.scene = Scene(root)
            initModality(Modality.WINDOW_MODAL)
            initOwner(parent.window)
        }
        stage.showAndWait()
    }

    private fun configWindow(root: Parent, title:String) {
        val stage = Stage()
        with(stage){
            this.title = title
            this.scene = Scene(root)
        }
        stage.showAndWait()
    }

    fun getScene(event: ActionEvent) : Scene = (event.source as Node).scene
    fun getStage(event: ActionEvent) : Stage = (getScene(event).window as Stage)

}