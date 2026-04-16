# [Корутины](https://kotlinlang.org/docs/coroutines-basics.html)

## Внутри

- [CoroutineContext](#coroutinecontext)
- [CoroutineContext.Key](#coroutinecontextkey)
- [CoroutineContext.Element](#coroutinecontextelement)


- [Job](#job)


- [Structured concurrency](#structured-concurrency)


- [Channels](#channel)

### CoroutineContext

Совокупность CoroutineContext.Element включающая:

- Dispatcher
- Job (жизненный цикл)
- CoroutineName
- ExceptionHandler
- Могут быть самописные

Имеет поведение элементов схожее с Map:
У двух Dispatchers один Key - новый элемент перезаписывает старый

```kotlin
val context = Dispatchers.IO + Dispatchers.Main // Dispatchers.Main
```

### CoroutineContext.Key

Ключ для поиска Element в контексте

```kotlin
interface CoroutineContext.Key<E : Element>

val context: CoroutineContext = continuation.context
val job: Job? = context[Job]
```

### CoroutineContext.Element

Хранит единице контекста и имеет ключ типа CoroutineContext.Key

```kotlin
interface CoroutineContext.Element : CoroutineContext
```

### Job

- элемент CoroutineContext
- отвечает за жизненный цикл корутины
- реализует cancellation + hierarchy

```kotlin
interface Job : CoroutineContext.Element
```

Состояния:
New -> Active -> Completing -> Completed -> Cancelling -> Cancelled

Через Job можно установить флаг отмены через `job.cancel()`

job.join() // Suspends, пока job не завершится

job.cancelAndJoin() // отменить и дождаться завершения

job.isActive
job.isCompleted
job.isCancelled

### Structured concurrency

Structured concurrency — это подход, при котором жизненный цикл всех
корутин привязан к scope, в которой они запущены.

То есть:

- корутины не «живут сами по себе»
- они не могут утечь (leak)
- их отмена и ошибки управляются централизованно

#### Примеры unstructured concurrency:

```kotlin
fun load() {
    thread {
        // что-то делаем
    }
}
```

Здесь поток:

- не контролируется вызывающим кодом
- может пережить lifecycle (например Activity)
- ошибки теряются

Тоже плохо — scope создан вручную и не контролируется
```kotlin
fun load() = CoroutineScope(Dispatchers.IO).launch {
    // работа
}

// Или GlobalScope
// scope.cancel() не отменит GlobalScope.launch
// если scope умер, GlobalScope.launch продолжит работу
// Ошибки теряются
fun main() {
    val scope = CoroutineScope(Job())
    scope.launch {
        GlobalScope.launch { // разрывает иерархию корутин (не child)
            // work
        }
    }
}
```

#### Основные принципы Structured Concurrency

Родитель → дети (иерархия)

Каждая корутина:
- имеет родителя (Job)
- может иметь детей

```kotlin
scope.launch {
    launch {
        // child coroutine
    }
}
```

Иерархия:
```
Job (scope)
 └── Job (launch)
      └── Job (child launch)
```

Отмена распространяется вниз - Все дочерние корутины отменяются автоматически

```kotlin
val job = scope.launch {
    launch {
        delay(1000)
        println("Never printed")
    }
}

job.cancel()
```

Ошибки распространяются вверх - Вся scope падает
```kotlin
coroutineScope {
    launch {
        throw RuntimeException("Error")
    }
}
```

### Channel

hot stream для обмена данными между корутинами (аналог BlockingQueue).