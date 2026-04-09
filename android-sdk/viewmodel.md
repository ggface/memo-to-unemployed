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

VM (ViewModel) переживает поворот, потому что она привязана к ViewModelStore, 
который хранится в ActivityThread.mActivities 
и не уничтожается при пересоздании Activity
ViewModel связана с ActivityStore, а не с конкретным экземпляром Activity