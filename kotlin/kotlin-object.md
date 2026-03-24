# Kotlin object

[Документация en](https://kotlinlang.org/docs/object-declarations.html)

## Варианты объявления

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

