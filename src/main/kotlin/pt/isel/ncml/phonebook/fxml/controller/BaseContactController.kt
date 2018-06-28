package pt.isel.ncml.phonebook.fxml.controller

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.scene.control.TextInputDialog
import javafx.scene.layout.AnchorPane
import pt.isel.ncml.phonebook.gui.FX
import pt.isel.ncml.phonebook.model.ContactEntry
import pt.isel.ncml.phonebook.model.ContactGroup
import pt.isel.ncml.phonebook.model.ContactInfo
import pt.isel.ncml.phonebook.model.NO_GROUP

enum class EditResult {Success, Cancel}

class BaseContactController {

    @FXML private lateinit var txtName : TextField
    @FXML private lateinit var txtNumber : TextField
    @FXML private lateinit var txtCountry : TextField
    @FXML private lateinit var txtAddress : TextField
    @FXML private lateinit var cmbGroup : ComboBox<ContactGroup>
    @FXML private lateinit var cmbType : ComboBox<ContactInfo.Type>
    @FXML private lateinit var cmbType2 : ComboBox<ContactInfo.Type>
    lateinit var entry : ContactEntry
    var editResult = EditResult.Cancel

    fun onSave(event: ActionEvent) {
        editResult = EditResult.Success
        entry = saveToEntry()
        FX.getStage(event).close()
    }

    fun onCancel(event: ActionEvent) {
        editResult = EditResult.Cancel
        FX.getStage(event).close()
    }

    fun init(entry: ContactEntry, groups : Array<ContactGroup>){
        this.entry = entry.copy()
        initCombos(groups)
        bind(this.entry)
    }

    private fun initCombos(groups: Array<ContactGroup>) {
        cmbType.items.addAll(ContactInfo.Type.Work, ContactInfo.Type.Home)
        cmbType.selectionModel.select(0)
        cmbType2.items.addAll(cmbType.items)
        cmbType2.selectionModel.select(0)
        cmbGroup.items.addAll(setOf(NO_GROUP, *groups, newGroup))
        cmbGroup.setOnAction(this::onCmbGroupClick)
    }

    private fun onCmbGroupClick(event: ActionEvent) {
        val selectedItem = cmbGroup.selectionModel.selectedItem
        if(selectedItem === newGroup){
            TextInputDialog("New Group Name").showAndWait().ifPresent {
                val element = ContactGroup(it)
                cmbGroup.items.add(element)
                cmbGroup.selectionModel.select(element)
            }
        }
    }

    private fun saveToEntry() : ContactEntry {
        val e = ContactEntry(txtName.text)
        e.group = cmbGroup.selectionModel.selectedItem
        savePhone(e)
        saveEmail(e)
        return e
    }

    private fun saveEmail(e: ContactEntry) {
        if(!txtAddress.text.isEmpty())
            e.contacts.add(ContactInfo.Email(txtAddress.text, cmbType2.selectionModel.selectedItem))
    }

    private fun savePhone(e: ContactEntry) {
        if(!txtCountry.text.isEmpty() || !txtNumber.text.isEmpty())
            e.contacts.add(ContactInfo.Telephone(txtCountry.text, txtNumber.text.toInt(), cmbType.selectionModel.selectedItem))
    }

    private fun bind(entry: ContactEntry) {
        txtName.text = entry.name
        cmbGroup.selectionModel.select(entry.group)
        entry.contacts.forEach {
            when(it){
                is ContactInfo.Telephone -> bind(it)
                is ContactInfo.Email -> bind(it)
            }
        }
    }

    private fun bind(telephone: ContactInfo.Telephone) {
        txtCountry.text = telephone.country
        txtNumber.text = telephone.number.toString()
        cmbType.selectionModel.select(telephone.type)
    }

    private fun bind(email: ContactInfo.Email) {
        txtAddress.text = email.address
        cmbType2.selectionModel.select(email.type)
    }

}

private val newGroup = ContactGroup("New...")

object ContactBoxFactory {
    private val fxmlName = "/fxml/base_contact.fxml"

    fun contactBox(contact: ContactEntry, parent: Scene, groups: Array<ContactGroup>) : ContactEditResult {
        val resource = javaClass.getResource(fxmlName)
        val controller = FX.launchModal<AnchorPane, BaseContactController>(parent, resource, "Contact"){
            it.init(contact, groups)
        }
        return ContactEditResult(controller.editResult, controller.entry)
    }
}

data class ContactEditResult(val result:EditResult, val contact:ContactEntry)
