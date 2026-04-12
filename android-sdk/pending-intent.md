# PendingIntent

```java
public final class PendingIntent implements Parcelable
```

`PendingIntent` это механизм в Android,
в котором мы создаем `Intent` и даем другому процессу,
это может быть другое приложение или сервис,
запустить его позже.

Работает благодаря ActivityManagerService

Например:
- пользователь нажал на уведомление
- сработал AlarmManager
- система вызывает BroadcastReceiver
- виджет на домашнем экране
- действия в Foreground Service
- под капотом WorkManager

👉 Во всех этих случаях система не имеет прямого доступа к твоему коду, поэтому используется PendingIntent.

Создание:
```kotlin
PendingIntent.getActivity(...)
PendingIntent.getService(...)
PendingIntent.getBroadcast(...)
PendingIntent.getBroadcast(...)
```

Флаги:
🔐 FLAG_IMMUTABLE vs FLAG_MUTABLE

Если не указать один из них будет IllegalArgumentException

С Android 12 это критично:
- FLAG_IMMUTABLE → Intent нельзя изменить (безопаснее ✅)
- FLAG_MUTABLE → можно изменить (нужно, например, для inline reply)

👉 По умолчанию всегда используй IMMUTABLE, если не нужен mutable.

♻️ FLAG_UPDATE_CURRENT

Обновляет существующий PendingIntent (обновляет extras внутри существующего)

🚫 FLAG_CANCEL_CURRENT
Удаляет старый и создаёт новый

Equals:
PendingIntent сравнивается не по extras, а по:

action
data
type
class
categories

👉 extras игнорируются ❗

Это приводит к багам:
```kotlin
// Эти два PendingIntent могут считаться одинаковыми!
intent.putExtra("id", 1)
intent.putExtra("id", 2)
```

✅ Как исправить
Использовать уникальный requestCode:
```kotlin
PendingIntent.getActivity(context, id, intent, FLAG_IMMUTABLE)
```
Или менять data:
```kotlin
intent.data = Uri.parse("myapp://item/$id")
```

При открытии Activity из уведомления кривой back stack:
При нажатии назад в открывшемся экране приложение закрывается
потому что у нас нет доступа к back stack и task приложения
экран запускается в новой таске.

Нужно использовать TaskStackBuilder для искусственного бекстека:
```kotlin
val intent = Intent(context, DetailsActivity::class.java)

val pendingIntent = TaskStackBuilder.create(context).run {
    addNextIntentWithParentStack(intent)
    getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
}
```
Нужно доработать манифест:
```xml
<activity
    android:name=".DetailsActivity"
    android:parentActivityName=".MainActivity" />
```
При нажатии назад откроется MainActivity.

Можно и явно задать стек:
```kotlin
TaskStackBuilder.create(context).run {
    addNextIntent(Intent(context, MainActivity::class.java))
    addNextIntent(Intent(context, DetailsActivity::class.java))
    getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
}
```