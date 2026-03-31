# Synchronized

### Что делает `@Synchronized` в Kotlin

`@Synchronized` — это JVM-аннотация, которая:

- Генерирует synchronized-метод на уровне байткода
- Использует monitor текущего объекта (this)
- Для companion object — использует monitor класса

По сути:

```kotlin
@Synchronized
fun increment() {
    counter++
}
```

Компилируется примерно как:

```java
public synchronized void increment() {
    counter++;
}
```

### Какую проблему решает

`@Synchronized` решает проблему:

Одновременного доступа нескольких потоков к критической секции

Гарантии:

- В один момент времени метод может выполняться только одним потоком
- Есть happens-before гарантия (видимость изменений между потоками)

То есть он решает:

- race condition
- нарушение инвариантов
- неконсистентное состояние объекта

### Когда его правильно использовать

1) Защита mutable state внутри одного инстанса

```kotlin
class Counter {
    private var value = 0

    @Synchronized
    fun increment() {
        value++
    }

    @Synchronized
    fun get(): Int = value
}
```
Если несколько потоков работают с одним экземпляром, это корректно.