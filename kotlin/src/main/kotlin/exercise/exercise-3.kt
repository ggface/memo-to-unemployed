import Person.Companion.obtainFullName

/**
 * Задача с делегатом.
 *
 * @author Ivan Novikov on 2026-04-12.
 */
interface Base {
    val message: String
    fun print()
}

open class BaseImpl : Base {
    override val message = "BaseImpl message"
    override fun print() { println(message) }
}

class Derived(b: Base) : Base by b {
    override val message = "Derived message"
}

class Inheritance : BaseImpl() {
    override val message = "Derived message"
}

fun main() {
    val b = BaseImpl()
    val derived = Derived(b)
    derived.print() // BaseImpl message

    val inheritance = Inheritance()
    inheritance.print() // Derived message
}