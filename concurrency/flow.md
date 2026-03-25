# Flow

Холодный поток данных который может вычисляться асинхронно

```kotlin
public interface Flow<out T> {
    public suspend fun collect(collector: FlowCollector<T>)
}
```

