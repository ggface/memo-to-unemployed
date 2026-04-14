# Immutable

Mutable объекты опасны, потому что создают скрытые 
побочные эффекты и ломают предсказуемость состояния. 

Это особенно критично в многопоточности, при работе с 
коллекциями (из-за hashCode), 
в реактивных подходах (Flow, Compose), 
где изменения могут не отслеживаться. 

Поэтому в большинстве случаев предпочтительны immutable объекты, 
а мутабельность должна быть строго инкапсулирована.

### Проблемы в многопоточности

```kotlin
data class User(var name: String)

val user = User("John")

Thread {
    user.name = "Alice"
}.start()

println(user.name)
```

Ты не контролируешь момент изменения. Что может случиться:

- race condition
- inconsistent state
- сложно воспроизводимые баги

👉 В coroutines это ещё хуже, состояние непредсказуемо:

```kotlin
val user = User("John")

launch {
    user.name = "Alice"
}

launch {
    user.name = "Bob"
}
```

Вывод:
Делая класс immutable, не получится "тихо" изменить объект,
только создать новый. Придется реализовать механизм уведомления
об изменении.

### Проблемы с кэшированием и коллекциями

Критичный кейс — HashMap / Set

```kotlin
data class User(var name: String)

val user = User("John")
val set = hashSetOf(user)

user.name = "Alice"

println(set.contains(user)) // ❌ может быть false!
```

Почему:

- hashCode() зависит от name
- ты изменил поле → изменился hash
- объект "потерялся" в коллекции

### Нарушение инвариантов объекта

Объект становится неконсистентным.

Контроль изменений = защита инвариантов

```kotlin
class BankAccount(var balance: Int)

// Кто угодно может сделать:
account.balance = -1_000_000

// Лучше сделать так
class BankAccount(private var balance: Int) {

    fun withdraw(amount: Int) {
        require(balance >= amount)
        balance -= amount
    }
}
```

### Проблемы в Jetpack Compose

Compose может:

- не заметить изменения
- не сделать recomposition

```kotlin
data class UiState(var name: String)
```

### Проблемы с Flow / StateFlow

Flow не эмитит новое значение потому что ссылка не изменилась

```kotlin
val state = MutableStateFlow(User("John"))

// Неправильно
state.value.name = "Alice"

// Правильно
state.value = state.value.copy(name = "Alice")
```

### Проблемы в тестах
Mutable объекты:
- сложно мокать
- сложно проверять
- тесты становятся flaky

### Когда mutable допустим
- Локальные переменные
- инкапсуляция объекта, но immutable снаружи