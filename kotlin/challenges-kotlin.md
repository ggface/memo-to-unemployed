# Задачки Kotlin

[Задача на конструкторы и init #1](./src/main/kotlin/exercise/exercise-1.kt)

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