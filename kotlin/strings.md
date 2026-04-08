# Строки

### Способы конкатенации

Оператором:

```kotlin
public actual operator fun plus(other: Any?): String

val a = "Hello"
val b = "World"
val result = a + " " + b // "Hello World"
```

Явный вызов функции plus()

```kotlin
val result = "Hello".plus(" World") // "Hello World"
```

StringBuilder / StringBuffer

- StringBuffer потокобезопасный аналог StringBuilder, но медленнее.
- Идеально для циклов и больших наборов строк.

```kotlin
val builder = StringBuilder()
builder.append("Hello")
builder.append(" ")
builder.append("World")
val result = builder.toString() // "Hello World"
```

buildString

- StringBuilder под капотом
- работает через функцию, где аргумент это фунциональный тип с ресивером

```kotlin
public inline fun buildString(builderAction: StringBuilder.() -> Unit): String

val result = buildString {
    append("Hello")
    append(" ")
    append("World")
} // "Hello World"
```

String Templates (Шаблоны строк)

- Компилируется в эффективный байткод, иногда со StringBuilder под капотом.

```kotlin
val name = "John"
val greeting = "Hello, $name!" // "Hello, John!"
val complex = "2 + 2 = ${2 + 2}" // "2 + 2 = 4"
```

joinToString() для коллекций

- Можно задать prefix, postfix, transform функцию.

```kotlin
val list = listOf("Kotlin", "Java", "Swift")
val result = list.joinToString(separator = ", ") // "Kotlin, Java, Swift"

list.joinToString(
    prefix = "[",
    postfix = "]",
    separator = "; "
) { it.uppercase() } // "[KOTLIN; JAVA; SWIFT]"
```

reduce / fold на коллекциях

- reduce не работает на пустых коллекциях.
- fold безопасен и позволяет задавать начальное значение.

```kotlin
val words = listOf("I", "love", "Kotlin")
val result = words.reduce { acc, s -> "$acc $s" } // "I love Kotlin"
val resultFold = words.fold("") { acc, s -> "$acc $s" }.trim() // "I love Kotlin"
```

String.format() (Java стиль)

```kotlin
val name = "Alice"
val age = 30
val result = String.format("My name is %s and I am %d", name, age)
// "My name is Alice and I am 30"
```

Java String.concat()

```java
String string = "Hello".concat("World"); // "HelloWorld"
```

concatToString() для CharArray или CharSequence

```kotlin
val chars = charArrayOf('H', 'e', 'l', 'l', 'o')
val result = chars.concatToString() // "Hello"
```

### StringBuffer
StringBuffer потокобезопасен за счёт синхронизации (synchronization) всех своих публичных методов.

```java
public synchronized StringBuffer append(String str) {
    // изменение внутреннего массива символов
}
```
- при вызове метода поток захватывает монитор объекта (this)
- другие потоки не могут одновременно выполнять другие synchronized-методы этого же объекта

Однако такой код не thread safe.
Mежду length() и append() другой поток может изменить buffer.

```kotlin
// Bad
if (buffer.length() == 0) {
    buffer.append("Hello")
}

// Correct
synchronized(buffer) {
    if (buffer.length() == 0) {
        buffer.append("Hello")
    }
}
```



