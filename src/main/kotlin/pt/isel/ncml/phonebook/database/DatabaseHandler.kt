package pt.isel.ncml.phonebook.database

import pt.isel.ncml.objectivedb.ObjectiveDB
import pt.isel.ncml.objectivedb.query.AttrExpr
import pt.isel.ncml.objectivedb.query.AttrExprVisitor
import pt.isel.ncml.objectivedb.query.ExprImpl
import pt.isel.ncml.phonebook.model.PhoneBook
import java.util.regex.Pattern

class DatabaseHandler(val name:String) {
    private val database by lazy { DbHelper.openOrCreateDatabase(name) }

    /**
     * gets the phonebook from the database. there should be only one
     */
    fun getPhoneBook() : PhoneBook {
        return database.query()
                .allFrom(PhoneBook::class.java)
                .single()
    }

    /**
     * saves the phonebook to the database
     */
    fun savePhoneBook(phoneBook: PhoneBook) {
        database.manage(phoneBook)
    }

    /**
     * swaps any existing phonebook with another one
     */
    fun reset(phoneBook: PhoneBook) {
        database.query().allFrom(PhoneBook::class.java).forEach(database::unmanage)
        database.manage(phoneBook)
    }

    fun getQueryHandler() : QueryHandler {
        return QueryHandler(database)
    }
}

class QueryHandler(private val database: ObjectiveDB) {

    fun <T> getAll(clazz: Class<T>) : Collection<T> = database.query().allFrom(clazz)

    fun <T> getWithProp(clazz: Class<T>, proName:String, propValue:String) : Collection<T> {
        return database
                .query()
                    .from(clazz)
                        .where(StringEqExpr(proName, propValue))
                    .end()
                .query()
    }

}

private class StringEqExpr(override val fieldName: String, override val value: Any) : AttrExpr {
    override fun accept(visitor: AttrExprVisitor) {
        visitor.visit(ExprImpl.Equals(StringRegexWrapper(value.toString()), fieldName))
    }
}

private class StringEqWrapper(private val value:Any) {

    override fun equals(other: Any?): Boolean {
        val otherString = other?.toString()
        return value.toString() == otherString
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

private class StringRegexWrapper(regex:String) {

    private val pattern = Pattern.compile(regex)

    override fun equals(other: Any?): Boolean {
        val otherString = other?.toString()
        return pattern.matcher(otherString).matches()
    }

    override fun hashCode(): Int {
        return pattern.hashCode()
    }
}