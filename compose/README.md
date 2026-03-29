# Compose

- [Composable](#composable)
- [Compose Runtime](#compose-runtime)
- [Side Effects](#side-effects)

## Что исследовать
interface Composition

@StateFactoryMarker
public fun <T> mutableStateOf(
    value: T,
    policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy(),
): MutableState<T> = createSnapshotMutableState(value, policy)

@Stable
public interface State<out T> {
    public val value: T
}

public inline operator fun <T> State<T>.getValue(thisObj: Any?, property: KProperty<*>): T = value

public inline operator fun <T> MutableState<T>.setValue(
    thisObj: Any?,
    property: KProperty<*>,
    value: T,
) {
    this.value = value
}

@Stable
public interface MutableState<T> : State<T> {
    override var value: T

    public operator fun component1(): T

    public operator fun component2(): (T) -> Unit
}

public fun <T> mutableStateListOf(): SnapshotStateList<T> = SnapshotStateList<T>()

public fun <K, V> mutableStateMapOf(): SnapshotStateMap<K, V> = SnapshotStateMap<K, V>()

public fun <T> mutableStateSetOf(): SnapshotStateSet<T> = SnapshotStateSet<T>()

## Composable

Что важно понимать:

Это не просто аннотация, а вход в Compose runtime.

Composable-функция:
- может вызываться только из другой composable
- не должна иметь сайд-эффектов
- должна быть [идемпотентной](../glossary/README.md#idempotent-идемпотентный)

Важно знать:
- recomposition происходит при изменении State
- composable должна быть максимально "pure"

## Compose Runtime
библиотека `androidx.compose.runtime`

Это не UI toolkit.
Это реактивный движок, который:

- хранит состояние
- управляет recomposition
- отслеживает чтение state
- управляет coroutine scope
- управляет эффектами
- управляет памятью composable

UI (Material, Layout и т.д.) — это уже поверх runtime.

## Side Effects
[Документация en](https://developer.android.com/develop/ui/compose/side-effects)

В Jetpack Compose под side effects понимают любые действия, которые выходят за рамки чистого вычисления UI и могут повлиять на внешний мир или состояние вне композиции.

### [LaunchedEffect](https://developer.android.com/develop/ui/compose/side-effects#launchedeffect)
Что это: может запустить корутину при входе в композицию или при изменении ключей. Корутина отменяется при выходе из композиции.

Выполняетсяя на `Dispatchers.Main.immediate`

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
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(viewModel)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(viewModel)
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
Без него — замкнётся устаревшая версия колбэка.

```kotlin
val onTimeoutUpdated by rememberUpdatedState(onTimeout)

LaunchedEffect(Unit) {
    delay(1000)
    onTimeoutUpdated()
}
```

Под капотом:
```kotlin
@Composable
public fun <T> rememberUpdatedState(newValue: T): State<T> = remember {
    mutableStateOf(newValue)
}.apply {
    value = newValue
}
```

### [produceState](https://developer.android.com/develop/ui/compose/side-effects#producestate)
//TODO перепроверить и обновить описание
Что это: создаёт State и управляет его значением внутри корутины.

На самом деле это замена LiveData для Compose.

Для чего:

- загрузка данных асинхронно
- объединение Flow → State
- создание state-driven логики

Что делает:

- мостик между suspend API и Compose State
- внутри — корутина
- возвращает State<T>

📌 Кейсы:

- простая загрузка данных
- адаптация Flow / suspend-функций

Пример:
```kotlin
val uiState by produceState<UiState>(UiState.Loading) {
    value = repository.load()
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
