/**
 * Задача на конструкторы и init #1.
 *
 * @author Ivan Novikov on 2026-04-12.
 */
class Student(var name: String) {

    private var id: Int = 0

    init {
        println("init name=$name id=$id")
    }

    constructor(name: String, id: Int) : this(name) {
        this.id = id
        println("init name=$name id=$id")
    }
}

/**
 * Вывод:
 *
 * init name=Вася Пупкин id=0
 * constructor name=Вася Пупкин id=23
 */
fun main() {
    Student("Вася Пупкин", 23)
}
