package pt.isel.ncml.phonebook.fxml.controller

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.control.SelectionMode
import javafx.scene.control.TextField
import javafx.stage.FileChooser
import pt.isel.ncml.phonebook.database.DatabaseHandler
import pt.isel.ncml.phonebook.gui.FX
import pt.isel.ncml.phonebook.log.LOGGER
import pt.isel.ncml.phonebook.model.ContactEntry
import pt.isel.ncml.phonebook.model.PhoneBook
import java.io.*

const val DB_NAME = "Phonebook.odb"

/**
 * Window backed by a database with a phonebook instance. There should be only one phonebook within app lifetime
 * Can load or save the phonebook to a file for convenience. When londing from the file swaps any existing instance from the database
 * for the current one
 */
class MainController {

    @FXML private lateinit var _contactEntryList: ListView<ContactEntry>
    @FXML private lateinit var nameSearch : TextField
    private var phoneBook : PhoneBook = PhoneBook()
    private val database = DatabaseHandler(DB_NAME)

    /**
     * FXML initialize, see [javafx.fxml.Initializable]
     */
    fun initialize() {
        _contactEntryList.selectionModel.selectionMode = SelectionMode.SINGLE
    }

    /**
     * Remove item from table
     */
    fun onRemove(event:ActionEvent) {
        val item = getSelectedItem()
        if(null != item){
            removeItem(item)
        } else {
            val source = event.source as Button
            sendMessage("No item selected", source.scene)
        }
    }

    /**
     * launch edit for new
     */
    fun onNew(event: ActionEvent) {
        val parent = FX.getScene(event)
        val (r, e) = ContactBoxFactory.contactBox(ContactEntry(""), parent, phoneBook.groupArray)
        if(r == EditResult.Success){
            phoneBook.add(e)
            bindPhoneBook()
        }
        LOGGER.log("NewContact[result:%s; data:%s]", r.toString(), e.toString())
    }

    /**
     * changes table values according to the name selected
     */
    fun onSearch() {
        val name = nameSearch.text?:""
        if(name.trim().isEmpty()){
           bindPhoneBook()
        } else {
            phoneBook.bindPhoneBook(_contactEntryList, {it.name.contains(name)})
        }
    }

    /**
     * Loads a file previously saved to the file system.
     * Resets the database to work with the object returned from the file
     */
    fun onLoadFile(event: ActionEvent) {
        val chooser = FileChooser()
        chooser.showOpenDialog(FX.getScene(event).window)
                ?.let(this::unmarshall)
                ?.run {
                    phoneBook = this
                    database.reset(phoneBook)
                    bindPhoneBook()
                }
    }

    /**
     * Saves the phonebook to a file
     */
    fun onSaveFile(event: ActionEvent) {
        val chooser = FileChooser()
        chooser.showSaveDialog(FX.getScene(event).window)
                ?.let { ObjectOutputStream(FileOutputStream(it)) }
                ?.use { it.writeObject(phoneBook) }

    }

    /**
     * saves the phonebook to the database
     */
    fun onSaveDatabase(){
        database.savePhoneBook(phoneBook)
    }

    private fun unmarshall(file: File) : PhoneBook {
        val input = ObjectInputStream(FileInputStream(file))
        return input.use {
            it.readObject() as PhoneBook
        }
    }

    /**
     * loads the phonebook from the database
     */
    fun onLoadDatabase() {
        phoneBook = EMPTY //so GC can fee old reference
        System.gc()
        System.gc()
        System.gc()
        phoneBook = database.getPhoneBook()
        bindPhoneBook()
    }

    /**
     * launches query window
     */
    fun onQuery(event: ActionEvent) {
        QueryGuiFactory.queryGui(FX.getScene(event), database.getQueryHandler())
    }

    private fun bindPhoneBook() {
        phoneBook.bindPhoneBook(_contactEntryList)
    }

    /**
     * launches edit window
     */
    fun onChange(event: ActionEvent) {
        val contact: ContactEntry? = getSelectedItem()
        val parent = FX.getScene(event)
        if(null == contact)
            MessageBoxFactory.newMessageBox("No items selected", "Warn", parent)
        else {
            val (r, e) = ContactBoxFactory.contactBox(contact, parent, phoneBook.groupArray)
            if(r == EditResult.Success){
                phoneBook.replace(contact, e)
                bindPhoneBook()
            }
            LOGGER.log("EditContact[result:%s; data:%s]", r.toString(), e.toString())
        }
    }

    private fun sendMessage(s: String, parent: Scene) {
        MessageBoxFactory.newMessageBox(s, "Error", parent)
    }

    private fun removeItem(item: ContactEntry) {
        phoneBook.remove(item)
        bindPhoneBook()
    }

    fun getSelectedItem() : ContactEntry? = _contactEntryList.selectionModel.selectedItem

}

private var EMPTY = PhoneBook()

private fun PhoneBook.bindPhoneBook(view: ListView<ContactEntry>, filter : (ContactEntry) -> Boolean = {true}) {
    view.items.clear()
    this.asList()
            .filter(filter)
            .sortedBy(ContactEntry::name)
            .forEach{ view.items.add(it) }
}


