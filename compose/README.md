# Compose

## Side Effects
[Документация en](https://developer.android.com/develop/ui/compose/side-effects)

### [LaunchedEffect](https://developer.android.com/develop/ui/compose/side-effects#launchedeffect)
Что это: может запустить корутину при входе в композицию или при изменении ключей. Корутина отменяется при выходе из композиции.

Для чего:
- однократные запросы данных
- запуск анимации
- подписка на изменения Flow/Channel
- выполнение асинхронной логики связанной с UI

### [DisposableEffect](https://developer.android.com/develop/ui/compose/side-effects#disposableeffect)
Что это: эффект, который требует очистки (dispose). Аналог onStart/onStop, onAttach/onDetach.

Для чего:

- регистрация listener’ов
- управление ресурсами
- подписка, требующая отмены

Пример:

```kotin
DisposableEffect(Unit) {
    val listener = ...
    view.addListener(listener)

    onDispose {
        view.removeListener(listener)
    }
}
```

### [SideEffect](https://developer.android.com/develop/ui/compose/side-effects#sideeffect-publish)
Что это: выполняется после успешной композиции, на главном потоке.

Для чего:

- синхронизировать состояние Compose со внешним миром (ViewModel, логгеры, системные API)
- неасинхронные действия

### [rememberCoroutineScope](https://developer.android.com/develop/ui/compose/side-effects#remembercoroutinescope)
Что это: даёт coroutine scope, привязанный к жизненному циклу current composition (не пересоздаётся на recomposition).

Для чего:

- запуск корутин по действиям пользователя (клики)
- запуск анимации вне LaunchedEffect

### [rememberUpdatedState](https://developer.android.com/develop/ui/compose/side-effects#rememberupdatedstate)
Обновляет ссылку на последнее значение, используется для корректной работы замыканий в LaunchedEffect/DisposableEffect.

Пример:
```kotlin
val onTimeoutUpdated by rememberUpdatedState(onTimeout)

LaunchedEffect(Unit) {
    delay(1000)
    onTimeoutUpdated()
}
```

### [produceState](https://developer.android.com/develop/ui/compose/side-effects#producestate)
Что это: создаёт State и управляет его значением внутри корутины.

На самом деле это замена LiveData для Compose.

Для чего:

- загрузка данных асинхронно
- объединение Flow → State
- создание state-driven логики

Пример:
```kotlin
val data by produceState(initialValue = emptyList()) {
    value = repository.loadData()
}
```

### [derivedStateOf](https://developer.android.com/develop/ui/compose/side-effects#derivedstateof)

Что это: вычисляет производное состояние лениво и эффективно, только когда меняются зависимости.

Для чего:

- оптимизация производных данных
- предотвращение лишних recomposition

Пример:
```kotlin
val filtered = remember(items) {
    derivedStateOf { items.filter { it.isActive } }
}
```

### [snapshotFlow](https://developer.android.com/develop/ui/compose/side-effects#snapshotFlow)

Что это: превращает Compose State в Flow, реагирующий на изменения snapshot-а.

Для чего:

- наблюдение за состоянием Compose из корутины
- debounce/throttle/flow-операции поверх Compose state

Пример:
```kotlin
LaunchedEffect(Unit) {
    snapshotFlow { searchQuery }
        .debounce(300)
        .collect { viewModel.search(it) }
}
```
