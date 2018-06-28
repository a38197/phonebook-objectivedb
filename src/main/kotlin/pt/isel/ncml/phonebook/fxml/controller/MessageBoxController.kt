package pt.isel.ncml.phonebook.fxml.controller

import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.control.TextArea
import javafx.scene.layout.AnchorPane
import pt.isel.ncml.phonebook.gui.FX

class MessageBox {

    @FXML private lateinit var textMessage : TextArea

    var text : String
        get() { return textMessage.text }
        set(value) { textMessage.text = value }

}

object MessageBoxFactory {

    fun newMessageBox(message: String, title:String, parent: Scene) {
        FX.launchModal<AnchorPane, MessageBox>(parent, javaClass.getResource("/fxml/message_box.fxml"), title){
            it.text = message
        }
    }

}