# Kotlin object

[Документация en](https://kotlinlang.org/docs/object-declarations.html)

### Факты

- Инициализация декларированного объекта является потокобезопасной и выполняется при первом обращении к объекту
- Анонимный объект инициализируется непосредственно при использовании
- Вспомогательный объект инициализируется при загрузке соответствующего класса, что соответствует семантике Java static initializer.
- От любого `object` нельзя наследоваться
- `object` может наследоваться от open или abstract классов, имплементить интефейсы

```kotlin
// Декларированный object 
object Singleton

// Вспомогательный объект (companion object)
class Product {
    
    companion object Factory {
        
        private const val LOG_TAG = "sys_product"
        
        fun create() : Product = Product()
    }
}

// Анонимный объект с супертипом
class MouseAdapter(logTag: String)

val listener = object : MouseAdapter("sys_click") {
    override fun mouseClicked(e: MouseEvent) = Unit
    override fun mouseEntered(e: MouseEvent) = Unit
}

// Анонимный объект (Object expressions)
fun foo() {
    val preferences = object {
        // Можем указать public, protected, internal - разницы как я понял нет
        val theme = "Dark"
    }
    val theme = preferences.theme
}
```

[Смотреть код](./src/main/kotlin/KotlinObject.kt)

### Data objects

В отличии от `object`, для `data object` генерятся:
- `toString()` можно переопределить
- `equals()` кастомный объявить нельзя 
- `hashCode()` кастомный объявить нельзя

Методы `copy()` и `componentN()` не генерятся. У нас синглтон и нет пропертей.

[Оф. дока](https://kotlinlang.org/docs/object-declarations.html#data-objects) говорит о том, что нужно обязательно сравнивать `data object` через `==` а не через сравнение по ссылке.
Пригодится если мы создадим другой экземпляр этого объекта через рефлексию.

