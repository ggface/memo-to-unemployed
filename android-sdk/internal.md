# Android runtime

💡 В новых Android:

AMS → про процессы

ATMS → про Activity/Task

Раньше всё было в AMS → стало слишком жирно → разделили.

### ActivityTaskManagerService (ATMS)
👉 Главный по Activity / UI навигации

Отвечает за:
- запуск Activity
- back stack (Task)
- windowing / multi-window
- lifecycle state machine

📌 Именно тут создаётся ActivityRecord

### ActivityManagerService (AMS)
👉 Главный по процессам и памяти

Отвечает за:
- запуск/убийство процессов
- приоритеты (foreground/background)
- services / broadcast'ы

📌 Делегирует UI-логику в ATMS

### ActivityRecord
👉 Системное описание Activity

Живёт в system_server

Хранит:
- state (RESUMED, etc.)
- Intent
- ссылку на процесс
- принадлежность к Task

### ApplicationThread
👉 Binder интерфейс приложения (сервер внутри app process)

Живет в app process.

```java
// В классе ActivityThread
final ApplicationThread mAppThread = new ApplicationThread();

class ApplicationThread extends IApplicationThread.Stub
```
📌 Через него system_server говорит приложению:

- "создай Activity"
- "вызови onResume"
- "pause её"

### ActivityThread
👉 Runtime приложения (главный orchestrator)

Живёт в app process (main thread)

Отвечает за:
- создание Activity
- вызов lifecycle методов
- работу main looper

📌 Он получает команды от ApplicationThread

Содержит main функцию
```java
public static void main(String[] args)
```

где вызывается
Looper.prepareMainLooper()
создается ActivityThread
создает ConfigurationController
создает Instrumentation
создает ContextImpl
создает наш Application и вызывает у него onCreate()

### Instrumentation
Это объект внутри app process, который выступает как прокси/контроллер 
между системой и приложением.

Он:
- запускает Activity
- вызывает lifecycle
- используется для тестов (AndroidJUnitRunner)

где живет: Создаётся внутри ActivityThread.
app process:
    ActivityThread
    └── Instrumentation
        └── Activity

При старте процесса:
Zygote fork → app process
    → ActivityThread.main()
        → ActivityThread.attach()
            → создаётся Instrumentation


lifecycle всегда идёт через Instrumentation

ActivityThread НЕ вызывает lifecycle напрямую. 

Вместо этого:
```java
mInstrumentation.callActivityOnCreate(activity, state);

mInstrumentation.callActivityOnResume(activity);
```
Когда мы вызываем 
```kotlin
startActivity(intent)
```

Под капотом
```
Activity.startActivity()
  → Instrumentation.execStartActivity()
    → Binder call 
        → ATMS
```

📌 То есть Instrumentation:
- точка входа в system_server
- оборачивает вызов
  
Полный flow с Instrumentation:
```
Activity.startActivity()
  → Instrumentation.execStartActivity()
    → ATMS (system_server)
      → ActivityRecord
      → ApplicationThread.scheduleLaunchActivity()
        → ActivityThread
          → Instrumentation.callActivityOnCreate()
            → Activity.onCreate()
```
