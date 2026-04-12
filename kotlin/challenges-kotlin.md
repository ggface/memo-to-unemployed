# Задачки Kotlin

- [Задача на конструкторы и init #1](#exercise-1) 
- [Задача на модификаторы доступа #1](#exercise-2)
- [Задача с делегатом](#exercise-3)

### Exercise 1

Требуется:
- Объяснить, как работают первичный и вторичный конструкторы.
- Указать, как вызываются эти конструкторы, и в каком порядке выполняются блоки init.
- Указать, какие данные передаются в свойства класса при создании объектов.

```kotlin
class Student(var name: String) {

    val x get() = y
    val y = 10
    
    init {
        // x будет 0 потому что y не успел проинициализироваться
        println("init name=$name x=$x")
    }

    constructor(sectionName: String, var id: Int) : this(sectionName) {
    }
}
```

Нужно помнить порядок инициализации в классе:
1. Вычисление аргументов конструктора
2. Инициализация свойств из primary constructor
3. Инициализация property в теле класса (сверху вниз)
4. Выполнение init блоков (сверху вниз, вперемешку с property)
5. Выполнение secondary constructor (если он был вызван)

Выводы:
- secondary constructor всегда последний по этому имеет доступ ко всем property даже ниже себя
- secondary constructor должен вызвать primary constructor явно или через другой secondary constructor
- только primary конструктор может иметь property (val/var)
- init блоки видят property только выше себя

[Дополнительно про backing filed](README.md#backing-fields)

[Код](./src/main/kotlin/exercise/exercise-1.kt)

### Exercise 2

```kotlin
data class Person(val firstName: String, private val secondName: String)

fun Person.fullName() = "${this.firstName} ${this.secondName}"

fun main() {
    println(Person("John", "Doe").fullName())
}
```

- не скомпилируется
- Extension function не имеет доступа к private property
- так же не поможет destructuring
- но можно изменить поле через copy:

```kotlin
val p1 = Person("John", "Doe")
val p2 = p1.copy(secondName = "Smith")
println(p2) // Person(firstName=John, secondName=Smith)
```

[Код](./src/main/kotlin/exercise/exercise-2.kt)

### Exercise 3

[Код](./src/main/kotlin/exercise/exercise-3.kt)

Почему не используется Derived.message?

Потому что:
- print() выполняется в BaseImpl
- а не в Derived
- значит this внутри print() = BaseImpl

Что бы изменить поведение:
```kotlin
// Класс Derived
override fun print() {
    println(message)
}
```