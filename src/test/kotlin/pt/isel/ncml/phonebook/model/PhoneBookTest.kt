package pt.isel.ncml.phonebook.model

import org.junit.Test
import pt.isel.ncml.phonebook.database.DbHelper

class PhoneBookTest {

    val otherGroup = ContactGroup("Other")

    @Test
    fun phonebookSaveRestore() {
        var pb: PhoneBook? = PhoneBook(
                ContactEntry("nuno", mutableListOf(ContactInfo.Telephone("PT",123456789,ContactInfo.Type.Home))),
                ContactEntry("marisa", mutableListOf(ContactInfo.Telephone("PT",987654321,ContactInfo.Type.Work)), otherGroup)
        )
        val pbId = pb!!.id
        DbHelper.singleUse {
            it.manage(pb!!)
            pb = null
            System.gc()
            System.gc()
            System.gc()
            pb = it.query().from(PhoneBook::class.java).where{it.id == pbId}.end().query().first()

            assert(pb != null)
            assert(pb?.get(0)?.name == "nuno")
            assert(pb?.get(0)?.contacts?.get(0)?.type === ContactInfo.Type.Home)
            assert((pb?.get(0)?.contacts?.get(0) as ContactInfo.Telephone).country == "PT")
            assert((pb?.get(0)?.contacts?.get(0) as ContactInfo.Telephone).number == 123456789)
            assert((pb?.get(0)?.group === NO_GROUP))
            assert(pb?.get(1)?.name == "marisa")
            assert(pb?.get(1)?.contacts?.get(0)?.type === ContactInfo.Type.Work)
            assert((pb?.get(1)?.contacts?.get(0) as ContactInfo.Telephone).country == "PT")
            assert((pb?.get(1)?.contacts?.get(0) as ContactInfo.Telephone).number == 987654321)
            assert((pb?.get(1)?.group === otherGroup))
        }
    }
}