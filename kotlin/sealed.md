# Sealed классы и интерфейсы

[en doc](https://kotlinlang.org/docs/sealed-classes.html)

TODO [gpt](https://chatgpt.com/share/69de01a7-9bc4-8329-bf7c-cb8cf887fdd8)

Обеспечивают контролируемое наследование в иерархии классов.
Все наследники известны на этапе компиляции.
Используя выражение `when` мы можем покрыть поведение для всех подклассов.

??? Прямые подклассы (Direct) — это классы, которые напрямую наследуют свой суперкласс.

??? Непрямые подклассы (Indirect) — это классы, которые наследуют от своего суперкласса более чем на один уровень ниже.


Sealed class является абстрактным классом и 
не может быть создан напрямую. 

Однако он может содержать конструкторы или наследовать их. 
Эти конструкторы предназначены не для создания экземпляров 
самого запечатанного класса, а для его подклассов. 

TODO
Конструкторы sealed классов могут иметь один из двух уровней 
видимости: `protected` (по умолчанию) или `private`:
```kotlin
sealed class IOError {
    // A sealed class constructor has protected visibility by default. It's visible inside this class and its subclasses
    constructor() { /*...*/ }

    // Private constructor, visible inside this class only.
    // Using a private constructor in a sealed class allows for even stricter control over instantiation, enabling specific initialization procedures within the class.
    private constructor(description: String): this() { /*...*/ }

    // This will raise an error because public and internal constructors are not allowed in sealed classes
    // public constructor(code: Int): this() {}
}
```

И интерфейсы и классы могут имплементить обычный интерфейс
sealed класс может наследоваться от sealed интерфейса
```kotlin
sealed interface Error
sealed class IOError(): Error
```

TODO  поля в интерфейсах и классах

```kotlin
interface F {
    
    val f: Int
}
sealed interface Error : F {
    val q: Int
}

sealed class IOError(): Error, E

class FileReadError(override val f: Int, val file: Int, wqe :String, override val q: Int): IOError()
```

TODO

А можем ли мы внутри, в иерархии добавить еще sell class?
То есть объявить sild-класс внутри sild-класса, да?

### Наследование

Прямые подклассы (Direct) запечатанных классов и интерфейсов должны быть 
объявлены в том же пакете.

TODO

Direct subclasses of sealed classes and interfaces must be declared in the same package. They may be top-level or nested inside any number of other named classes, named interfaces, or named objects. Subclasses can have any visibility as long as they are compatible with normal inheritance rules in Kotlin.

### Enum vs sealed
Каждая константа enum существует как single instance, в то время 
как подклассы sealed класса могут иметь несколько экземпляров.

Enum классы не могут наследоваться от sealed класса или 
любого другого класса. 

Однако они могут реализовывать sealed интерфейсы:
```kotlin
sealed interface Error

// enum class extending the sealed interface Error
enum class ErrorType : Error {
    FILE_ERROR, DATABASE_ERROR
}
```