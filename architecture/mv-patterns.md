# MV* паттерны

- [Паттерны для новичков: MVC vs MVP vs MVVM](https://habr.com/ru/articles/215605/)

Все MV* паттерны решают одни и те же задачи:
- разделение ответственности
- управление состоянием UI

| Паттерн  | Основная идея                                    |
| -------- | ------------------------------------------------ |
| **MVP**  | Presenter управляет View                         |
| **MVVM** | View подписывается на ViewModel                  |
| **MVI**  | Однонаправленный поток данных + единое состояние |

### MVC (Model–View–Controller)
// TODO

### MVP (Model–View–Presenter)
// TODO

### MVVM (Model–View–ViewModel)
// TODO

### MVI (Model–View–Intent)

- Intent: Пользовательские события
- Model/Reducer: Преобразование старого состояния в новое
- State: Единственный источник истины для View
- View: Рендерит состояние
  
View → Intents → ViewModel → State → View

// TODO Тема [Unidirectional data flow (UDF)](https://developer.android.com/develop/ui/compose/architecture#udf)

Чем MVI отличается от MVVM?

Ответ:

MVVM допускает императивные события и двусторонние связи.

MVI — строгий однонаправленный поток: Intent → Reduce → State → Render.

Зачем нужен единственный source of truth?

Ответ:
Чтобы избежать рассинхронизации между частями UI и упростить дебаг и тестирование.
