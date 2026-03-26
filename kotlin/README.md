# Kotlin

## Темы

- [object и companion object](kotlin-object.md)
- [data classes](data-class.md)

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

Мы можем создать подобные функции в любом классе

Для data-классов функции `componentN()` генерятся автоматически