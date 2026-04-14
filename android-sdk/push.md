# Push

🧩 Полный жизненный цикл

- App получает token
- Отправляет на backend
- Backend → FCM
- FCM → Google infra
- Google → device (GMS connection)
- GMS → Intent
- Firebase SDK → Service
- onMessageReceived

### Device install

Eсли на устройстве есть Google Play Services то при установке приложения
Firebase Cloud Messaging (FCM) SDK генерит необходимые идентификаторы для приложения
и запрашивает (remote) registration token, который привязан к:

- app instance
- device
- Firebase project

После чего SDK отправит нам в приложение новый токен
и мы сможем получить его через FirebaseMessagingService.onNewToken(newToken: String)

После чего нам следует оповестить наш бек и передать новый токен для нашего девайса.

P.S.: Токен обновляется при:

- установке МП
- обновлении МП
- стирании всех данных МП в настройках

### Backend

Как только бек получил наш токен, он может отправлять нам пуши.
Бек вызывает https POST запрос на fcm.googleapis.com
тело пуша выглядит примерно так:

```json
{
  "message": {
    "token": "fcm_device_token",
    "data": {
      "type": "chat",
      "messageId": "123"
    },
    "notification": {
      "title": "Hello",
      "body": "World"
    },
    "android": {
      "priority": "HIGH",
      "ttl": "60s"
    }
  }
}
```

### FCM

Когда FCM remote получает данные, валидируется токен,
если он протух, то на бек вернется ошибка,
если все ок, то определяется target device и сообщение
попадает в очередь сообщений для этого девайса с учетом приоритета пушей,
Движок доставки пуша учитывает:

- TTL
- priority
- device state (online/offline)
- Doze mode

### Пуш прилетает на устройство

На девайсе работает компонент Google Play Services, он хранит
постоянное соединение TCP-соединение с FCM remote.

Затем GMS принимает сообщение и решает что с ним делать,
в зависимости от типа пуша и его payload:

- Notification message
- Data message
- Mixed (notification + data)

Типы payload:

1. Notification message
    - НЕ вызывает `onMessageReceived`, если app в background или killed
    - GMS сам показывает notification через NotificationManager системы
    - но если foreground то `onMessageReceived` сработает

Как обработать клик такого пуша?
- GMS создаёт PendingIntent
- открывается LAUNCHER Activity
- extras прокидываются в intent

Ловить стоит в onCreate и onNewIntent (Если приложение уже открыто).

```json
{
  "message": {
    "token": "...",
    "notification": {
      "title": "Hello",
      "body": "World"
    }
  }
}
```

2. Data message
   всегда идёт в `FirebaseMessagingService.onMessageReceived()`

```json
 "data": {...}
 ```

3. Mixed (notification + data)
    
   Если МП в фоне то:

   - notification показывается системой
   - data → НЕ приходит в onMessageReceived но попадает в intent при клике

    Если МП на переднем плане
    
    - вызов `FirebaseMessagingService.onMessageReceived()`
    - можем показать сами

### Как вызывается FirebaseMessagingService

GMS отправляет intent `com.google.android.c2dm.intent.RECEIVE`
Приложение получает его через наш сервис

```xml

<service
        android:name=".MyFirebaseMessagingService"
        android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT"/>
    </intent-filter>
</service>
```

Firebase SDK:

- перехватывает intent
- парсит payload
- вызывает `onMessageReceived(RemoteMessage)`

```kotlin
override fun onMessageReceived(message: RemoteMessage)
```

📌 Важно:

- вызывается НЕ на main thread
- есть лимит времени (~10 сек)

### Priority и Doze mode

NORMAL priority:

- может задерживаться и вовсе откладываться если девайс спит
- batching

HIGH priority:

- пытается разбудить устройство
- может пробить Doze
