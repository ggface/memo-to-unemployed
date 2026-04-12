import Person.Companion.obtainFullName

/**
 * Задача на модификаторы доступа #1.
 *
 * @author Ivan Novikov on 2026-04-12.
 */
data class Person(val firstName: String, private val secondName: String) {
    val fName = "${this.firstName} ${this.secondName}"

    /**
     * Внутри Person работает как функция расширения
     * Снаружи видна только в контексте ресивера
     */
    fun Person.getFullName() = "${this.firstName} ${this.secondName}"

    companion object {
        fun Person.obtainFullName() = this.getFullName()
    }
}

fun main() {
    val person1 = Person("John", "Doe")
    val person2 = Person("Ян", "Новиков")
    person1.fName
//    person1.getFullName() // Нет доступа
    person1.obtainFullName()

    with(person2) { // не важно какой Person, мы получили ресивер через функциональный тип
        println(person1.getFullName()) // Но так доступ есть, вывод: John Doe
    }
}