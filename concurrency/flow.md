# Flow

### Базовый уровень — Flow
Холодный поток данных который может вычисляться асинхронно

- Ничего не происходит без collect
- Каждый новый подписчик запускает поток заново

```kotlin
public interface Flow<out T> {
    suspend fun collect(collector: FlowCollector<T>)
}
```

### FlowCollector
Приемник значений

```kotlin
public fun interface FlowCollector<in T> {
    suspend fun emit(value: T)
}

flow {
    emit(1) // Вызываем FlowCollector.emit
}
```

### Flow builders

Базовый билдер
```kotlin
fun <T> flow(block: suspend FlowCollector<T>.() -> Unit): Flow<T>
```

Билдер `flowOf` на базе `flow {}`
```kotlin
public fun <T> flowOf(vararg elements: T): Flow<T> = flow {
    for (element in elements) {
        emit(element)
    }
}

public fun <T> flowOf(value: T): Flow<T> = flow {
    emit(value)
}
```

Билдер `asFlow` на базе `flow {}`. 
Существуют конструкции для разнообразных перечислений на базе цикла внутри `flow {}`

```kotlin
public fun <T> Iterator<T>.asFlow(): Flow<T> = flow {
    forEach { value ->
        emit(value)
    }
}
```

### SharedFlow
мультикаст-поток (много подписчиков получают одни данные).

```kotlin
public interface SharedFlow<out T> : Flow<T> {
    
    public val replayCache: List<T>
    
    override suspend fun collect(collector: FlowCollector<T>): Nothing
}

public interface MutableSharedFlow<T> : SharedFlow<T>, FlowCollector<T> { 
    
    override suspend fun emit(value: T)

    public fun tryEmit(value: T): Boolean

    public val subscriptionCount: StateFlow<Int>

    public fun resetReplayCache()
}
```

### StateFlow

```kotlin
public interface StateFlow<out T> : SharedFlow<T> {
    public val value: T
}
```