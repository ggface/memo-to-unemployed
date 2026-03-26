# Data class

[Документация en](https://kotlinlang.org/docs/object-declarations.html)

### Сахар

Компилятор автоматически создает следующие элементы на основе всех property, 
объявленных в primary constructor:

* Пара `equals()`/`hashCode()`
* `toString()`
* [`componentN()`](README.md#деструктуризация)
* `copy()`

Primary constructor должен иметь минимум одну property.

Все аргументы конструктора должны быть val или var.

Дата-классы не могут быть abstract, open, sealed или inner.

Могут наследоваться от обычных классов, ограничения обычные

### Модификаторы

```kotlin
object Visibility {

    public data class Q1(val s: String) // ✅ По умолчанию и так public

    internal data class Q2(val s: String) // ✅

    protected data class Q3(val s: String) // ❌ Ошибка компиляции

    private data class Q4(val s: String) // ✅

}

object Inheritance {

    open data class Q1(val s: String) // ❌ Ошибка компиляции

    abstract data class Q2(val s: String) // ❌ Ошибка компиляции

    override data class Q3(val s: String) // ❌ Неприменимо к классам в принципе

    final data class Q4(val s: String) // ✅ По умолчанию и так final
}
```

### Копирование

При копировании можно получить доступ к private property

```kotlin
data class Person(
    private val age: Int,
    val name: String
)

fun main() {
    val copy = Person(17, "Маша").let {
        it.name // Очевидно есть доступ
        it.age // Ошибка компиляции, нет доступа

        it.copy(age = 27) // Создаем новый Person через копирование
    }
    println(copy) // Obj(age=27, name=Маша)
}
```