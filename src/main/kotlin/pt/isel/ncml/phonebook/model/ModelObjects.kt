package pt.isel.ncml.phonebook.model

import java.io.Serializable

class PhoneBook(vararg contacts: ContactEntry) : Serializable {

    private val contactList : MutableList<ContactEntry> = mutableListOf<ContactEntry>()
    private val groups = mutableSetOf<ContactGroup>(NO_GROUP)

    val id : Int

    init {
        contacts.forEach{contactList.add(it)}
        id = idGen++
    }

    fun add(contactEntry: ContactEntry): Boolean {
        groups.add(contactEntry.group)
        return contactList.add(contactEntry)
    }

    fun remove(contactEntry: ContactEntry): Boolean {
        val group = contactEntry.group
        val result = contactList.remove(contactEntry)

        if(NO_GROUP != group && contactList.count { it.group == group } == 0) //remove non used groups
            groups.remove(group)

        return result
    }

    fun replace(old: ContactEntry, new: ContactEntry) {
        groups.add(new.group)
        contactList.replaceAll { if(old === it) new else it }
    }

    operator fun get(index:Int) : ContactEntry = contactList[index]

    fun search(name: String) : Sequence<ContactEntry> {
        return contactList.asSequence().filter { it.name.contains(name) }
    }

    fun asList() : List<ContactEntry> {
        return ArrayList(contactList)
    }

    val groupArray : Array<ContactGroup>
        get() = groups.toTypedArray()

    private companion object {
        var idGen = 0

        @JvmStatic
        private val serialVersionUID : Long = 4843473548161158548
    }
}

data class ContactEntry(
        var name:String,
        val contacts : MutableList<ContactInfo> = mutableListOf(),
        var group : ContactGroup = NO_GROUP
) : Serializable

sealed class ContactInfo : Serializable {

    enum class Type { Home, Work }

    abstract var type : Type

    data class Telephone(var country:String, var number:Int, override var type: Type) : ContactInfo()
    data class Email(var address:String, override var type: Type) : ContactInfo()
}

/**
 * Data class for contact group. Its not an enum because it's intended to be added more groups
 */
data class ContactGroup(val name:String) : Serializable {
    override fun toString(): String {
        return name
    }
}
val NO_GROUP = ContactGroup("No group")
