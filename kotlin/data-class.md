# Data class

[Документация en](https://kotlinlang.org/docs/object-declarations.html)

### Сахар

Компилятор автоматически создает следующие элементы на основе всех property, 
объявленных в primary constructor:

* Пара `equals()`/`hashCode()`
* `toString()`
* `componentN()`
* `copy()`

Primary constructor должен иметь минимум одну property.

Все аргументы конструктора должны быть val или var.

Дата-классы не могут быть abstract, open, sealed или inner.

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

Инициализация объявления объекта является потокобезопасной и выполняется при первом обращении к объекту.

companion object инициализируется при загрузке соответствующего класса, что соответствует семантике Java static initializer.

```kotlin
// Самостоятельный object 
object Singleton

// Сопутствующий объект
class Product {
    
    companion object Factory {
        
        private const val LOG_TAG = "sys_product"
        
        fun create() : Product = TODO()
    }
}

// Анонимный объект
val listener = object : MouseAdapter() {
    override fun mouseClicked(e: MouseEvent) = TODO()
    override fun mouseEntered(e: MouseEvent) = TODO()
}

// Анонимный объект
fun foo() {
    val preferences = object {
        val theme = "Dark"
    }
    val theme = preferences.theme
}
```

[Смотреть код](./src/main/kotlin/KotlinObject.kt)

