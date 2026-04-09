# [Корутины](https://kotlinlang.org/docs/coroutines-basics.html)

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

Через Job можно установить флаг отметы через `job.cancel()`

job.join() // Suspends, пока job не завершится

job.cancelAndJoin() // отменить и дождаться завершения

job.isActive
job.isCompleted
job.isCancelled

### Channel
hot stream для обмена данными между корутинами (аналог BlockingQueue).