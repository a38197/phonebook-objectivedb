package pt.isel.ncml.phonebook.log

import java.time.LocalDateTime

object LOGGER {

    private val logMethod : (String)->Unit = System.out::println
    private enum class Level {DEBUG}

    fun log(msg:String, vararg params:Any) {
        doLog(Level.DEBUG, String.format(msg, *params))
    }

    private fun doLog(l:Level, m:String) {
        val date = LocalDateTime.now()
        val toLog = "$l:$date:$m"
        logMethod(toLog)
    }

}