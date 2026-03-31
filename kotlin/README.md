# Kotlin

### Отдельные Темы
- [object и companion object](kotlin-object.md)
- [data classes](data-class.md)
- [Исключения и паники](error.md)

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
- [Функции](#функции)
- [KClass](#функции)

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

Доступ к ним более эффективный так как не генерится геттер. 
Однако у них есть `backing field` с которым можно взаимодействовать через рефлексию так как под капотом будет сгенерен `static final field`  

Есть требования:
- Должны быть объявлены как top-level property, или как член класса (member) в object declaration или в companion object.
- Должны быть инициализированны примитивным типом (numbers, characters, booleans) или стрингой. Беззнаковые целые как UInt тоже можно.
- Не может иметь кастомный геттер

### Generics
💡 Самый простой способ понять

Задай себе вопрос:

Объект возвращает T? → out

Объект принимает T? → in

Делает и то и другое? → инвариантность

[Подробно о PECS](https://habr.com/ru/articles/559268/) (Producer Extends Consumer Super — из Java)

##### Инвариантность (по умолчанию)
```kotlin
class Box<T>(val value: T)

val catBox: Box<Cat> = Box(Cat())

val animalBox: Box<Animal> = catBox // ❌ Ошибка
```

##### Ковариантность (Producer → out) 
Тип только производит значение. Мы только читаем `T`, но не можем туда ничего записать.

```kotlin
class Cage<out T : Animal>(private val animal: T) {
    fun get(): T = animal
}
class Cage<out T>(var animal: T) // ❌ Ошибка

val catCage: Cage<Cat> = Cage(Cat())
val animalCage: Cage<Animal> = catCage // ✅ Работает
```

##### Контрвариантность (Consumer → in)
Тип только потребляет значения.

```kotlin
interface AnimalPrinter<in T> {
    fun print(animal: T)
}

val animalPrinter: AnimalPrinter<Animal> = object : AnimalPrinter<Animal> {
    override fun print(animal: Animal) {
        println(animal)
    }
}

val catPrinter: AnimalPrinter<Cat> = animalPrinter // ✅ Работает
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

### Функции

##### Trailing lambda
Если последний аргумент функции является функцией, 
то ее можно вынести за скобки, это называется `trailing lambda`.

```kotlin
fun greeting(userId: Int, message: () -> Unit) = Unit

// Используем
greeting(2048) {
    // Мы внутри замыкающей лямбды (trailing lambda)
    println("Hello!") 
}
```

##### Infix notation
```kotlin
public infix fun <A, B> A.to(that: B): Pair<A, B> = Pair(this, that)

// mapOf в данном случае принимает vararg pairs: Pair<K, V>
val map = mapOf(
    "key1" to "value1",
    "key2" to "value2"
)
```

### Функции
`KClass` — это класс из Kotlin Reflection API, который представляет метаинформацию о Kotlin-классе во время выполнения. Если говорить просто — это Kotlin-версия `java.lang.Class`.
```kotlin
// Получаем KClass из Kotlin класса
val kClass = MyClass::class

// Получаем KClass из Java класс
val kClass = javaClass.kotlin
```

🔹 Что можно делать через KClass?
1️⃣ Получать имя
```kotlin
User::class.simpleName
```
2️⃣ Получать конструкторы
```kotlin
User::class.constructors
```
3️⃣ Создавать экземпляр (если есть пустой конструктор)
```kotlin
val instance = User::class.createInstance()
```

Требует dependency:
```kotlin
implementation("org.jetbrains.kotlin:kotlin-reflect")
```
🔹 Почему в Kotlin сделали отдельный KClass?

Потому что:

Java reflection не знает ничего о:
- nullability
- data class
- sealed class
- object
- companion object
- inline class

Kotlin reflection хранит Kotlin-метаданные.

### Scope функции (Функции области видимости)
Нужны для выполнения блока кода для объекта, на котором вызывается функция.

```
        this        it
self    apply       also
result  run / with  let
```
- apply: донастройка объекта, замена паттерна Builder
```kotlin
Intent(context, AccountDetailsActivity::class.java).apply { // this: Intent
    putExtra(EXTRA_ACCOUNT_ID, accountId)
    // Возвращаем this (Intent)
}
```
- also: сайд эффект для объекта
```kotlin
private val initialId: Int = savedStateHandle[KEY].also { // it: Int
    Timber.tag(LOG_TAG).v("initialId is $it")
    // Возвращаем this (Int)
}
```
- with: помогает для вызова функций в контексте объекта, над которым вызвали
```kotlin
val density = LocalDensity.current
val cornerRadiusDp: Dp = with(density) { // this: Density
    outerFloat.toDp() // Вызываем Float.toDp() из Density
    // Возвращаем result (Dp)
}
```


