# ☕ Java Core

## 📚 Статьи

- [Object](./object.md)
- [Сериализация](./serialization.md)
- [Копирование объектов](./object-copying.md)
- [Коллекции](./collections.md)

### Типы ссылок

##### Strong Reference
- Объект не будет удалён GC, пока на него есть хотя бы одна сильная ссылка.
- Если obj = null, объект становится кандидатом на удаление (если нет других сильных ссылок).

##### WeakReference
- Объект удаляется GC при следующем запуске сборщика, даже если память ещё не заполнена.
- Часто используется в WeakHashMap для хранения временных данных.

##### Soft Reference
- Объект удаляется GC только при нехватке памяти (перед OutOfMemoryError).
- Полезно для кеширования, где данные можно пересоздать.

##### Phantom Reference

```java
ReferenceQueue<Object> queue = new ReferenceQueue<>();
PhantomReference<Object> phantomRef = new PhantomReference<>(new Object(), queue);
// После GC:
Reference<?> removedRef = queue.poll();  // Получить удалённую ссылку
```
- Объект уже удалён, но фантомная ссылка остаётся в очереди (ReferenceQueue).
- Позволяет выполнить финализацию (например, освобождение ресурсов) после удаления объекта.
- В отличие от finalize(), не препятствует сборке мусора.

##### Важно
- `finalize()` не рекомендуется использовать, так как он замедляет GC и может "воскресить" объект.
- Для управления ресурсами лучше использовать `AutoCloseable` + `try-with-resources` (в Java) или `use` (в Kotlin).
- `PhantomReference` + `ReferenceQueue` — более надёжная замена finalize().

Любой класс, реализующий этот интерфейс, может использоваться в try-with-resources конструкции.

```java
public interface AutoCloseable {
    void close() throws Exception;
}
```

Про сборку мусора можно почитать [здесь (GC)](../jvm/README.md#gc)