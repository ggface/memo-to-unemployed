# Kotlin

### Отдельные Темы
- [object и companion object](kotlin-object.md)
- [data classes](data-class.md)

### База внутри
- [Properties](#properties)
- [Backing fields](#backing-fields)
- [Backing properties](#backing-properties)
- [Константы](#compile-time-constants)

### Темы внутри
- [Generics](#generics)
- [Деструктуризация](#деструктуризация)
- [Any, Nothing, Unit](#any-nothing-unit)
- [Делегаты](#делегаты)

### Properties
Переменные `val/val` называем `properties`, они же _свойства_. 
Для таких полей автоматом генерятся `accessors` - это геттер и для `var` сеттер.

Если объявить property в top-level файла, ее нужно считать глобальной переменной, принадлежащей пакету:

```kotlin
// File: Constants.kt
package my.app

val pi = 3.14159
var counter = 0
```

Использовать property до инициализации нельзя.
Инициализировать можно при объявлении и позже если позволяет компилятор
Можно объявлять в файлах, классах, интерфейсах и объектах.

Можно запилить кастомный геттер/сеттер:
- Будет вычисляться при каждом обращении
- Тип можно не указывать если компилятор может определить его по месту вызова
- Кастомный сеттер не вызывается при инициализации
```kotlin
class Rectangle(val width: Int, val height: Int) {
    val area get() = this.width * this.height
}

class Point(var x: Int, var y: Int) {
    var coordinates: String
        get() = "$x,$y"
        set(value) {
            val parts = value.split(",")
            x = parts[0].toInt()
            y = parts[1].toInt()
        }
}
```

Чтобы изменить видимость у `accessor`, нужен модификатор перед ключевым словом `get` или `set`:
```kotlin
class BankAccount(initialBalance: Int) {
    var balance: Int = initialBalance
        private set
}
```

К аксессорам можно добавлять анотации
```kotlin
@Target(AnnotationTarget.PROPERTY_GETTER)
annotation class Inject

class Service {
    var dependency: String = "Default Service"
        @Inject get
}
```

### Backing fields
В kotlin проперти хранят значение в памяти используя `backing fields`. 

Объявлять их ручками нельзя. 

Котлин генерит их только при необходимости:
- Используем геттер и сеттер по умолчанию
- Используем ключевое слово `field` в любом из аксессоров

Получить доступ можно используя ключевое слово `field` в аксессорах.

```kotlin
// Здесь backing field не будет сгенерирован
val isEmpty: Boolean
    get() = this.size == 0

// Здесь будет
class Scoreboard {
    var score: Int = 0
        set(value) {
            field = value
            println("Score updated to $field")
        }
}
```

### Backing properties
Бывает мы хотим, чтобы свойство было доступно только для чтения вне класса, 
но при этом у нас должен быть способ напрямую изменять свойство.

```kotlin
// Backing property
private val _backingProperty = MutableStateFlow("")
// Public read-only view
val property : StateFlow<String> = _backingProperty
```

Может быть полезен еще и для нескольких публичных пропертей с одним backing property в качестве источника данных
```kotlin
class Temperature {
    // Backing property storing temperature in Celsius
    private var _celsius: Double = 0.0

    var celsius: Double
        get() = _celsius
        set(value) { _celsius = value }

    var fahrenheit: Double
        get() = _celsius * 9 / 5 + 32
        set(value) { _celsius = (value - 32) * 5 / 9 }
}

```

[нейминг с _ легален](https://kotlinlang.org/docs/coding-conventions.html#names-for-backing-properties)

### Compile-time constants
Если значение рид онли проперти известно на этапе компиляции стоит исп модификатор const.

Compile-time константы встраиваются (инлайнятся) во время компиляции. 

Доступ к ним более эффективный так как не генерится геттер. Однако у них есть `backing field` с которым можно взаимодействовать через рефлексию 

Есть требования:
- Должны быть объявлены как top-level property, или как член класса (member) в object declaration или в companion object.
- Должны быть инициализированны примитивным типом (numbers, characters, booleans) или стрингой. Беззнаковые целые как UInt тоже можно.
- Не может иметь кастомный геттер

### Generics

Ковариантность (producer) 

```kotlin
val b: Box<Animal> = Box<Cat>(Cat())
```

Контрвариантность (consumer)

```kotlin
val p: Processor<Int> = Processor<Number>()
```

### Деструктуризация
Основывается на функциях `operator fun`

Для получения `(key, value)` используются две функции `component1` и `component1` в коде `Map.Entry`.
```kotlin
fun foo() {
    emptyMap<String, String>().mapValues { (key, value) -> "$value!" }
}

// operator fun для деструктуризации Map.Entry 

public inline operator fun <K, V> Map.Entry<K, V>.component1(): K = key

public inline operator fun <K, V> Map.Entry<K, V>.component2(): V = value
```

Мы можем создать подобные функции в любом классе, 
а так же деклирировать как функции-расширения в других файлах/классах;

Для data-классов функции `componentN()` генерятся автоматически

Если в супер классе data класса объявить функцию `componentN()`, 
то компилятор будет ругаться на data-класс.
`open class` + `override operator fun` не помогут.
```kotlin
open class SimpleClass {
    operator fun component1(): String = ""
}

// ругается 
// Function 'fun component1(): String' generated for 
// the data class conflicts with the supertype member 'fun component1(): String' defined in '/SimpleClass'.
data class SimpleDataClass(
    val s: String
) : SimpleClass()
```

### Any Nothing Unit
TODO

### Делегаты
[TODO](https://kotlinlang.org/docs/delegated-properties.html)