# Android SDK

## Снаружи
- [View](./view.md)
- [Parcelable](./parcelable.md)

## Внутри
- [Activities](#activity)
- [Services](#service)
- [Broadcast receivers](#broadcastreceiver)
- [Content providers](#contentprovider)

- [The manifest file](#manifest)
- [Fragment](#fragment)
- [Bundle]()
- [Intent]()
- [Сохранение состояния]()
- [Запуск приложения](#application-launch)
- [Context, Binder IPC, Zygote](#context-binder-ipc-zygote)

## Что исследовать
один и тот же объект для парселабла или сериализации при передаче через интент

ограниения bundle

передача ссылочных типов через intent и bundle

жц вью, методы, инвалидейт, и реквест лейаут, viewgroup и ее особенности

жц фрагментов, разница иинициализации

можно ли поменять размер вью без layoutparams черех width и heigth

RecyclerView ListView ViewHolders и как сделать эффективные списки

https://developer.android.com/reference/androidx/recyclerview/widget/DiffUtil.Callback

Коллекции android sdk

### Activity

// TODO вызов методов ЖЦ при переходе с экрана на экран + сделать визуал в excalidraw

(https://github.com/fylmr/android-interview?tab=readme-ov-file#activity)

// TODO поворот экрана

### Service

### BroadcastReceiver

### ContentProvider

### Manifest

### Fragment
(https://github.com/fylmr/android-interview?tab=readme-ov-file#fragments)

### Application Launch

Материалы:
- [Схема запуска](./images/app-launch.webp)
- [Android App Launch: A Deep Dive](https://kitemetric.com/blogs/android-app-launch-a-deep-dive)
- [Inside Android: Context, Binder IPC, Zygote - Ioannis Anifantakis | droidcon Berlin 2025](https://youtu.be/AXUu-_fEyD0?si=Z4xQKhbKJTaFflsD)


#### 1. The Launcher's Role
   Your journey begins with the Launcher—the home screen app. When you tap an app icon:

- Touch Processing: The Android input framework identifies the touch event and the targeted icon.
- Intent Creation: The Launcher generates an Intent—a messaging object—specifying the app to launch.
- System Communication: The Intent is sent to the Activity Manager Service (AMS) via Binder Inter-Process Communication (IPC).

#### 2. Activity Manager Service (AMS): The Orchestrator
   The AMS, a core Android system component, now takes control:

- App Status Check: It verifies if the app is already running. If so, it brings it to the foreground.
- Process Existence: Otherwise, it checks if the app's process exists.
- Process Creation: If not, AMS instructs Zygote to create a new process for the app.

#### 3. Zygote: The Efficient Process Factory
   Zygote, a crucial system process, optimizes app launching by:

- Request Handling: It receives the app launch request from AMS via a socket message.
- Forking: Instead of creating a process from scratch, Zygote forks itself, producing a quick, lightweight copy.
- ART Initialization: This new process prepares to load the app using the Android Runtime (ART).

#### 4. App Initialization: Android Runtime (ART) Takes Over
   With the app's process ready, ART manages:

- Resource Loading: It loads the app's code and resources into memory.
- Main Method Execution: The entry point, `ActivityThread.main()`, starts the main thread and event loop.
- Application Object Creation: The Application class, defined in `AndroidManifest.xml`, is instantiated, initiating its `onCreate()` method.

#### 5. Displaying the First Screen
   Finally, the app's initial activity is launched:

- AMS Instruction: AMS instructs the app process to start its initial activity.
- ActivityThread Handling: `ActivityThread` manages the activity's creation, executing lifecycle methods such as `onCreate()`, `onStart()`, and `onResume()`.
- UI Rendering: `ViewRootImpl` and `SurfaceFlinger` handle OpenGL rendering and display the UI on your screen, synchronized with the screen's refresh rate using Choreographer.

#### 6. Your App Appears!
   The final visual integration:

- UI Synchronization: The UI updates align with the screen's refresh rate.
- SurfaceFlinger Composition: `SurfaceFlinger` combines all visual elements and sends the final frame to your phone's display.
- App Launch Completion: Your app is now ready for use!

### Context, Binder IPC, Zygote
Что происходит при запуске иконки ланчера или устанавливаем МП?

При установке МП ему присваивается уникальный идентификатор юзера Linux

Каждое МП изолировано от других МП. Все его ресурсы недоступны для других МП.

По сути и у самого приложения нет доступа к его же ресурсам. Что бы получить ресурсы, нам нужно обраться к `Context`.



