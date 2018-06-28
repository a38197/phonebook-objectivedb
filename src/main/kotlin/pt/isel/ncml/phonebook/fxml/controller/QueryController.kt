package pt.isel.ncml.phonebook.fxml.controller

import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.control.ChoiceBox
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import pt.isel.ncml.phonebook.database.QueryHandler
import pt.isel.ncml.phonebook.gui.FX
import pt.isel.ncml.phonebook.model.ContactEntry
import pt.isel.ncml.phonebook.model.ContactGroup
import pt.isel.ncml.phonebook.model.ContactInfo

class QueryController {

    @FXML private lateinit var chItemType : ChoiceBox<TypeFilter>
    @FXML private lateinit var txtPropName : TextField
    @FXML private lateinit var txtPropValue : TextField
    @FXML private lateinit var lstResult : ListView<String>
    private lateinit var handler : QueryHandler

    /**
     * [javafx.fxml.Initializable]
     */
    fun initialize() {
        chItemType.items.addAll(TypeFilter.values())
        chItemType.selectionModel.selectFirst()
        chItemType.setOnAction {
            txtPropName.clear()
            txtPropValue.clear()
        }
    }

    fun init(query: QueryHandler) {
        handler = query
    }

    fun onApplyFilter() {
        val clazz : Class<*> = getSelectedClass()
        val result = if(txtPropName.text.isEmpty())
            handler.getAll(clazz)
        else
            handler.getWithProp(clazz, txtPropName.text, txtPropValue.text)
        bindResult(lstResult, result)
    }

    private fun getSelectedClass(): Class<*> {
        return chItemType.selectionModel.selectedItem.clazz
    }

    private fun bindResult(lstResult: ListView<String>, result: Collection<Any>) {
        lstResult.items.clear()
        lstResult.items.addAll(result.map(Any::toString))
    }

}

private enum class TypeFilter(val clazz: Class<*>) {

    Contact(ContactEntry::class.java),
    Phone(ContactInfo.Telephone::class.java),
    Email(ContactInfo.Email::class.java),
    Group(ContactGroup::class.java);

}

object QueryGuiFactory {
    private val fxmlName = "/fxml/query.fxml"

    fun queryGui(parent: Scene, handler:QueryHandler) {
        val resource = javaClass.getResource(fxmlName)
        FX.launch<AnchorPane, QueryController>(parent, resource, "Query", {it.init(handler)})
    }

}