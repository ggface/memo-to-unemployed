# Kotlin

## Темы

- [object и companion object](kotlin-object.md)

### Generics

Ковариантность (producer) 

```kotlin
val b: Box<Animal> = Box<Cat>(Cat())
```

Контрвариантность (consumer)

```kotlin
val p: Processor<Int> = Processor<Number>()
```