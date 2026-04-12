# Kotlin

### Отдельные Темы
- [object и companion object](kotlin-object.md)
- [data classes](data-class.md)
- [Исключения и паники](error.md)
- [Коллекции](collections.md)
- [Строки](strings.md)
- [Задачки](challenges-kotlin.md)

### База внутри
- [Properties](#properties)
- [Backing fields](#backing-fields)
- [Backing properties](#backing-properties)
- [Константы](#compile-time-constants)
- [lateinit var](#lateinit)

### Темы внутри
- [Generics](#generics)
- [Деструктуризация](#деструктуризация)
- [Any, Nothing, Unit](#any-nothing-unit)
- [Делегирование](#делегирование)
- [Функции](#функции)
- [KClass etc (Reflection Api)](#kclass)
- [Scope function (Функции области видимости)](#scope-функции-функции-области-видимости)

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

// Здесь будет 
val isCorrect: Boolean
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

такой код в Java будет скомпилирован примерно так:
```java
public final class YourClass {
    // private поле
    private final MutableStateFlow<String> _backingProperty = new MutableStateFlow<>("");
    // public поле (НО через getter)
    private final StateFlow<String> property = _backingProperty;
    // getter для property
    public final StateFlow<String> getProperty() {
        return this.property;
    }
}
```

но если мы напишем так:
```kotlin
// Backing property
private val _backingProperty = MutableStateFlow("")
// Public read-only view
val property : StateFlow<String> 
    get() = _backingProperty
```

такой код в Java будет скомпилирован примерно так:

```java
public final class YourClass {
    // backing field есть
    private final MutableStateFlow<String> _backingProperty = new MutableStateFlow<>("");
    // getter без поля
    public final StateFlow<String> getProperty() {
        return this._backingProperty;
    }
}
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

#### lateinit
`lateinit var any: Any` - это отложенная инициализация var без nullable.
При обращении до инициализации throw `UninitializedPropertyAccessException`
- только var
- только non-null типы
- не для примитивов (Int, Boolean и т.д.), но String можно

TODO А можно lateinit inline data class?

Можно чекать инициализацию
```kotlin
lateinit var example: String

fun check(){
    ::example.isInitialized
}
```

В этом примере `::example` это property reference
Доступен он через Reflection API

```kotlin
// Из пример выше
val prop: KMutableProperty1<A, String> = A::example
val func: KFunction1<A, Unit> = A::check
```

`KProperty` и `KFunction` — это основные интерфейсы `Kotlin Reflection`,
которые представляют ссылки на свойства и функции соответственно
и позволяют получать их метаданные и вызывать их динамически.

Подробнее про [рефлексию](#kclass)

### Generics
Самый простой способ понять

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
`open class` + `open operator fun` это исправят.
```kotlin
open class SimpleClass {
    open operator fun component1(): String = ""
}

// ругается 
// Function 'fun component1(): String' generated for 
// the data class conflicts with the supertype member 'fun component1(): String' defined in '/SimpleClass'.
data class SimpleDataClass(
    val s: String
) : SimpleClass()
```

### Any Nothing Unit

#### Any
Any это базовый тип всех не-nullable типов в Kotlin.

`Any` и `Any?` в JVM мапится в `java.lang.Object`

Имеет методы:
- equals() // принципы те же что и для [Java Object.equals()](../java-core/object.md#принципы-equals)
- hashCode()
- toString()

В дженериках `fun <T> foo(x: T) { }` T будет `T : Any?`, если хочется убрать optional, нужно использовать `fun <T : Any> foo(x: T)`.

#### Nothing
Nothing — тип, который никогда не имеет инстанса.

```kotlin
inline fun TODO(): Nothing = throw NotImplementedError()
```

Но так как Nothing это подтип любого типа, мы можем иметь и такую сигнатуру:
```kotlin
inline fun TODO(): String = throw NotImplementedError()

val x: String = throw Exception()

fun loop(): Nothing { while (true) {} }

val result: String = when (value) {
    1 -> "One"
    2 -> "Two"
    else -> throw IllegalArgumentException("Invalid")  // Тип `throw` — Nothing
} 
```

Однако мы можем объявить `val nothingNull: Nothing? = null`, в таком случае пропертя может иметь только одно значение - null.

В JVM `Nothing?` мапится в `ava.lang.Void`, а для `Nothing` мапинга нет.

Есть интересная деталь:
```kotlin
// Корректно, хотя emptyList() это List<Nothing>
val myList: List<String> = emptyList()
```
Это работает благодаря [ковариантности](#generics).

#### Unit
Unit это singleton объект. В байткоде создаётся `Unit.INSTANCE`.

Unit описывает отсутствие значимого результата, может принимать только значение `Unit`.

```kotlin
val unitValue: Unit = Unit  // Корректно
val list: List<Unit> = listOf(Unit) // Корректно
```

В JVM мапится в `void` если возвращается из функции и в `kotlin.Unit` если используется как тип.

### Делегирование
Delegation (делегирование) — это паттерн, при котором объект передаёт часть своей ответственности другому объекту.

Есть два типа делегатов:
- Class delegation
- Property delegation

#### Class delegation:
Kotlin позволяет делегировать реализацию интерфейса другому объекту через ключевое слово by. Это альтернатива наследованию без необходимости вручную проксировать методы.

Пример:
```kotlin
interface Logger {
    fun log(message: String)
}

class ConsoleLogger : Logger {
    override fun log(message: String) {
        println("Console: $message")
    }
}

// Добавляем поведение, не переписывая Logger
class TimestampLogger(
    private val logger: Logger
) : Logger by logger {

    override fun log(message: String) {
        logger.log("${System.currentTimeMillis()}: $message")
    }
}
```

👉 Что важно подчеркнуть:
- мы расширили поведение, не меняя ConsoleLogger
- это composition over inheritance
- не нужно писать прокси-методы

#### Property delegation
Для работы делегатов свойств нужны два метода getValue для val и setValue для var

Можно реализовать как функции в классе, так и extension functions снаружи класса

Еще один способ реализовать делегат свойств это анонимный объект
используя интерфейсы 
[ReadOnlyProperty](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.properties/-read-only-property/) или 
[ReadWriteProperty](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.properties/-read-write-property/)

```kotlin
fun resourceDelegate(
    resource: Resource = Resource()
): ReadWriteProperty<Any?, Resource> =
    object : ReadWriteProperty<Any?, Resource> {
        var curValue = resource
        
        override fun getValue(thisRef: Any?, property: KProperty<*>): Resource = curValue
        
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Resource) {
            curValue = value
        }
    }

val readOnlyResource: Resource by resourceDelegate()  // ReadWriteProperty as val
var readWriteResource: Resource by resourceDelegate()
```

И есть способ реализовать делегат через `operator fun provideDelegate`
[en doc](https://kotlinlang.org/docs/delegated-properties.html#providing-a-delegate)

```kotlin
import kotlin.reflect.KProperty

class Delegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "$thisRef, thank you for delegating '${property.name}' to me!"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("$value has been assigned to '${property.name}' in $thisRef.")
    }
}

class Example {
    var p: String by Delegate()
}
```

Еще можно делегировать property другому property через `::` квалификатор.
Делегатом может быть свойство нашего класса, другого класса или top-level свойство.
[подробнее en](https://kotlinlang.org/docs/delegated-properties.html#delegating-to-another-property)

```kotlin
var newName: Int = 0
@Deprecated("Use 'newName' instead", ReplaceWith("newName"))
var oldName: Int by this::newName
```

Еще можно хранить проперти в Map [en doc](https://kotlinlang.org/docs/delegated-properties.html#storing-properties-in-a-map)

работает с val + Map и с var + MutableMap

```kotlin
class User(val map: Map<String, Any?>) {
    val name: String by map
    val age: Int     by map
}

fun main() {
    val user = User(mapOf(
        "name" to "John Doe",
        "age"  to 25
    ))

    println(user.name) // Prints "John Doe"
    println(user.age)  // Prints 25
}
```

Еще можно объявить переменную как delegated properties:
Переменная memoizedFoo будет вычислена только при первом обращении к ней. 
Если someCondition не выполняется, переменная вообще не будет вычисляться.

```kotlin
fun example(computeFoo: () -> Foo) {
    val memoizedFoo by lazy(computeFoo)

    if (someCondition && memoizedFoo.isValid()) {
        memoizedFoo.doSomething()
    }
}
```

#### Стандартные делегаты в котлин:
##### Lazy properties
Kotlin lazy — это делегат свойства, реализованный через интерфейс Lazy<T>:
Работает только с `val`

```kotlin
public actual fun <T> lazy(initializer: () -> T): Lazy<T>
```

это делегат свойства, реализованный через интерфейс Lazy<T>

```kotlin
public interface Lazy<out T> {
    val value: T
    fun isInitialized(): Boolean
}
```

- вычисляется при первом доступе
- результат кешируется

По умолчанию SYNCHRONIZED (thread safe)

Вариации LazyThreadSafetyMode:
- SYNCHRONIZED: Только один поток вычисляет значение, остальные ждут
- NONE: Не синхронизирован, быстрый доступ в single-thread
- PUBLICATION: может выполниться несколько раз (Несколько потоков могут вычислить значение, берётся первый результат)

SYNCHRONIZED ≠ блокировка на весь класс,
используется volatile + synchronized(this) внутри делегата.

##### Observable properties
Вызывает callback после каждого изменения property

```kotlin
var name: String by Delegates.observable("initial value") {
    prop, old, new ->
    // свойство уже изменено
    println("$old -> $new")
}
```

##### Vetoable properties
Вызывает callback при попытке изменить и дает возможность наложить вето на изменение

```kotlin
var max: Int by Delegates.vetoable(0) { property, oldValue, newValue ->
    // вызовется перед присвоением нового значения свойству
    newValue > oldValue
}
```

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

### KClass
`KClass` — это класс из Kotlin Reflection API, который представляет метаинформацию о Kotlin-классе во время выполнения. 
Если говорить просто — это Kotlin-версия `java.lang.Class`.

Оператор `::` создаёт ссылку на свойство `KProperty`, 
а не возвращает его значение.
Свойство `isInitialized` доступно только у этой ссылки, 
потому что оно относится к метаданным lateinit-переменной, 
а не к самому значению.

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

Так же в Reflection Api есть:


`KProperty`: ссылка на свойство (val/var)
  - `KProperty0` — без receiver
  - `KProperty1` — с receiver
  - `KMutableProperty` — для var

Можно получить:
- prop.name
- prop.returnType
- prop.annotations

`KFunction`: ссылка на функцию

Можно получить:
- fn.name
- fn.parameters
- fn.returnType

`KCallable`: общий интерфейс для `KProperty` и `KFunction`

`KType`: описание типа включая generic параметры и nullable.

```kotlin
val type: KType = typeOf<List<String?>>() // List<String?>

type.classifier         // List::class
type.arguments          // [String?]
type.isMarkedNullable   // false
```

`KParameter`: параметр функции или конструктора

Можно получить:
- имя
- тип
- позиция
- optional / vararg

Пример с методом
```kotlin
fun greet(name: String, age: Int) {
    println("Hello $name, age $age")
}

val fn = ::greet

fn.parameters.forEach { param ->
    println("name=${param.name}, type=${param.type}")
}
```

Пример с конструктором
```kotlin
class User(val name: String, val age: Int)

val ctor = User::class.constructors.first()

ctor.parameters.forEach {
    println("name=${it.name}, type=${it.type}")
}
```

### Scope функции (Функции области видимости)
Нужны для выполнения блока кода для `context object`, на котором вызывается функция.

Все scope функции файла StandardKt:
`let`, `with`, `run`, `apply`, `also` и так же в оф доке упоминаются `takeIf` and `takeUnless`.

Доп инфо:
`(T) -> R` — обычная функция с аргументом
`T.() -> R` — функциональный тип с ресивером (`T` будет доступен через `this`)

```
        this        it
self    apply       also
result  run / with  let
```

#### let: преобразования и null-check

- Объект контекста доступен как аргумент (it).
- Возвращает результат лямбды.

```kotlin
inline fun <T, R> T.let(block: (T) -> R): R {
    return block(this)
}

val result = "hello".let { it.uppercase() }

val name: String? = getName()
name?.let {
    println(it.length)
}
```

#### with: помогает для вызова функций в контексте объекта, над которым вызвали

- Объект контекста доступен как ресивер (this).
- Возвращает результат лямбды.

```kotlin
inline fun <T, R> with(receiver: T, block: T.() -> R): R {
    return receiver.block()
}

val density = LocalDensity.current
val cornerRadiusDp: Dp = with(density) { // this: Density
    outerFloat.toDp() // Вызываем Float.toDp() из Density
    // Возвращаем result (Dp)
}
```

#### run: 

- Объект контекста доступен как ресивер (this).
- Возвращает результат лямбды.
- Имеет аналог без функции-расширения 

```kotlin
inline fun <T, R> T.run(block: T.() -> R): R {
    return block()
}

val result = "hello".run { // Здесь this = "hello"
    uppercase() // Можно обращаться без this
}
```

#### apply: донастройка объекта, замена паттерна Builder

- Объект контекста доступен как ресивер (this).
- Возвращает объект контекста.

```kotlin
inline fun <T> T.apply(block: T.() -> Unit): T {
    block()
    return this
}

Intent(context, AccountDetailsActivity::class.java).apply { // this: Intent
    putExtra(EXTRA_ACCOUNT_ID, accountId)
    // Возвращаем this (Intent)
}
```
#### also: сайд эффект для объекта

- Объект контекста доступен как аргумент (it).
- Возвращает объект контекста.

```kotlin
inline fun <T> T.also(block: (T) -> Unit): T {
    block(this)
    return this
}

private val initialId: Int = savedStateHandle[KEY].also { // it: Int
    Timber.tag(LOG_TAG).v("initialId is $it")
    // Возвращаем this (Int)
}
```

#### `takeIf` и `takeUnless`

- Объект контекста доступен как аргумент (it).
- takeIf возвращает объект контекста если условие true, иначе null
- takeUnless возвращает объект контекста если условие false, иначе null

```kotlin
inline fun <T> T.takeIf(predicate: (T) -> Boolean): T? {
    return if (predicate(this)) this else null
}

inline fun <T> T.takeUnless(predicate: (T) -> Boolean): T? {
    return if (!predicate(this)) this else null
}

/**
 * 👉 если isValid == true → вернётся user
 * 👉 иначе → null
 */
val validUser = user.takeIf { it.isValid() }

/**
 * 👉 если isBanned == false → вернётся user
 */
val validUser = user.takeUnless { it.isBanned() }
```

