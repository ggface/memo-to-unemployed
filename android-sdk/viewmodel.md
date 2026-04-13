# ViewModel

### Как ViewModel переживает поворот экрана
WindowManagerService уведомляет ATMS/AMS внутри system_server о смене конфигурации

ActivityManagerService решает какие экраны нужно пересоздать

Каждое Activity имеет в AndroidManifest.xml атрибуты:
- android:configChanges
- android:screenOrientation

Если configChanges не обрабатывает ориентацию:
- Activity будет пересоздана
- Иначе вызывается onConfigurationChanged() без пересоздания

Через Binder IPC вызывает в app-процессе:
```java
IApplicationThread.scheduleDestroyActivity(token, finish=false)
```

```java
ActivityThread.handleDestroyActivity(ActivityClientRecord r)
```

ЖЦ в этот момент onPause() → onStop() → onSaveInstanceState(Bundle) → onDestroy()
- onSaveInstanceState формирует Bundle
- Состояние view hierarchy, SavedStateRegistry, FragmentManager сериализуются
  
Bundle отправляется через Binder обратно в system_server, в ActivityRecord.icicle

Теперь system_server сообщает ActivityThread о новом конфиге:
```java
IApplicationThread.scheduleLaunchActivity(clientTransaction)
```
Новый Configuration создаётся в system_server / AMS
В app-процессе ActivityThread обновляет Resources:
```java
Resources.updateConfiguration(config, metrics)
```

В ActivityThread:

Создаётся новый экземпляр Activity
Передаётся Bundle savedInstanceState из ActivityRecord.icicle
Lifecycle:
- onCreate(Bundle savedInstanceState)
- onStart()
- onResume()

ViewModel переживает поворот экрана, потому что он хранится в ViewModelStore, 
который привязан к ViewModelStoreOwner (обычно Activity).
При configuration change Activity пересоздаётся, 
но через механизм onRetainNonConfigurationInstance() система сохраняет 
ViewModelStore и передаёт его в новую Activity через 
getLastNonConfigurationInstance().

Поэтому ViewModelProvider получает тот же самый ViewModel из 
существующего store.

vm живет в ViewModelStore,
который по сути хранит HashMap<String, ViewModel>()

Нашим ViewModelStore владеет интерфейс ViewModelStoreOwner, 
его реализует ComponentActivity (так же Fragment и NavBackStackEntry)

Внутри ComponentActivity есть
```java
NonConfigurationInstances mLastNonConfigurationInstances;
```

NonConfigurationInstances при пересоздании экрана будет храниться внутри
ActivityThread в ActivityClientRecord

перед уничтожением активити вызывается
```java
// Ты можешь вернуть объект, который переживёт конфиг-чейндж.
@Override
public Object onRetainNonConfigurationInstance() {
    return new NonConfigurationInstances(
            customObject,
            viewModelStore
    );
}
```

а в новой активити можно вызвать:
```java
// Возвращает то, что ты сохранил.
Object getLastNonConfigurationInstance()
```
новая активити:
- получает старый ViewModelStore
- и если у нас есть ранее созданные viewmodel, они будут переиспользованы

Как создаётся ViewModel
ViewModelProvider
```kotlin
val vm = ViewModelProvider(owner).get(MyViewModel::class.java)
```
Что он делает:
- Берёт ViewModelStore у owner
- Проверяет:
  - есть ли уже ViewModel по ключу
- Если есть → возвращает
- Если нет → создаёт через Factory и кладёт в store

Ключи ViewModel
```
"androidx.lifecycle.ViewModelProvider.DefaultKey:MyViewModel"
```
Можно задавать свои ключи

ViewModel переживает rotation
но НЕ переживает process death (для этого есть SavedStateHandle)

### Восстановление после убийства процесса
При process death ViewModel уничтожается, так как хранится в памяти.
Система сохраняет только Bundle через onSaveInstanceState.
При восстановлении Activity SavedStateRegistry восстанавливает данные из Bundle.
ViewModel создаётся заново через SavedStateViewModelFactory, который извлекает сохранённое состояние и передаёт его в SavedStateHandle.
Таким образом, состояние восстанавливается, но сам ViewModel — новый.


Activity вызывает onSaveInstanceState(Bundle)
ActivityThread сохраняет state
SavedStateRegistry собирает state от:
- FragmentManager
- ViewModel (через SavedStateHandle)
- других компонентов

ActivityTaskManagerService хранит данные состояния пока процесс мертв

Когда зигота создаст новый процесс
и отработает ActivityThread
SavedStateRegistry восстановит bundle
