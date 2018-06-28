package pt.isel.ncml.phonebook.database

import org.apache.commons.lang.math.RandomUtils
import pt.isel.ncml.objectivedb.Builder
import pt.isel.ncml.objectivedb.ObjectiveDB
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

object DbHelper {


    fun recreateDatabase(fileName:String): ObjectiveDB {
        deleteDatabase(fileName)
        return openOrCreateDatabase(fileName)
    }

    fun deleteDatabase(fileName:String) {
        assert(deleteRecursively(fileName))
    }

    fun openOrCreateDatabase(fileName: String) : ObjectiveDB {
        return Builder.dbBuilder().build(Builder.configBuilder().build(fileName))
    }

    private fun deleteRecursively(fileName: String): Boolean {
        val path = Paths.get(fileName)
        var ret = true
        if(Files.exists(path)){
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach{
                        ret = ret && it.delete()
                        println("Delete file $it : $ret")
                    }
        }
        return ret
    }

    fun singleUse( actions : (ObjectiveDB) -> Unit ) {
        val fileName = "temp${RandomUtils.nextInt()}"
        val db = recreateDatabase(fileName)
        try {
            actions(db)
        } finally {
            db.close()
            assert(deleteRecursively(fileName))
        }
    }

}