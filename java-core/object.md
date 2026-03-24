# Object

## Методы

- equals
- hashCode
- toString
- clone
- getClass
- finalize
- wait
- notify
- notifyAll

## Принципы equals

**Рефлексивность** — x.equals(x) возвращает true

**Симметричность** — x.equals(y) <=> y.equals(x)

**Транзитивность** — x.equals(y) <=> y.equals(z) <=> x.equals(z)

**Согласованность** — повторный вызов x.equals(y) должен возвращать значение предыдущего вызова, если сравниваемые поля не изменялись

**Сравнение с null** — x.equals(null) возвращает false

## 💻 Примеры

[Смотреть код](./code/ObjectExamples.kt)

## 🧠 Вопросы с собеседований

- Почему нужно переопределять hashCode вместе с equals?
- Что будет если нарушить контракт?