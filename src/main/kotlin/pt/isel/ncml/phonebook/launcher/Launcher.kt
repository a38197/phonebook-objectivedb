package pt.isel.ncml.phonebook.launcher

import javafx.application.Application
import pt.isel.ncml.phonebook.gui.PhoneBook

fun main(args: Array<String>) {
    Application.launch(PhoneBook::class.java, *args)
}