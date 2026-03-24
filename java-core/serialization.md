# Сериализация

## 📌 Что это

Процесс преобразования объекта в поток байтов.

## 🔑 Интерфейсы

- `Serializable`
- `Externalizable`

## ⚠️ Подводные камни

- transient поля не сериализуются
- serialVersionUID обязателен

## 💻 Примеры

[Смотреть код](./code/SerializationExamples.kt)

## 🧠 Вопросы

- Что будет если serialVersionUID не совпадает?
- Почему Serializable — marker interface?