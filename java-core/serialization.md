# Сериализация

## 📌 Что это

Сериализация - процесс преобразования структуры данных в последовательность байт. В Java нужно имплементить интерфейс Serializable.

пакеты класса и полей класса

имена полей

занчения полей

под капотом использует Reflection API

@Transient для игнорирования полей

Можно кастомизировать

private void writeObject(java.io.ObjectOutputStream out) throws IOException

private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException;


Externalizable

Нужно иметь пустой конструктор для наполнения полей данными

быстрее Serializable

readExternal(ObjectInput in)

writeExternal(ObjectOutput out)

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