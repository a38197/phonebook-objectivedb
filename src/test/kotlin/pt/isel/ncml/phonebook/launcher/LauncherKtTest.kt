package pt.isel.ncml.phonebook.launcher

import org.junit.Test
import pt.isel.ncml.phonebook.database.DbHelper

class LauncherKtTest{

    @Test
    fun dbCreation() {
        DbHelper.singleUse { db ->
            db.manage(Any())
        }
    }

}