# SOLID

Плейлист с видосами: https://www.youtube.com/playlist?list=PLp_FpnyDwvuA9vzOImAAn4COaa9yK71hd

Мини-шпаргалка:
- S — одна ответственность
- O — расширяем без изменения
- L — подставляем без поломки
- I — маленькие интерфейсы
- D — зависим от абстракций

Примеры кода:
- [Single Responsibility Principle (SRP)](#single-responsibility-principle-srp)
- [Open/Closed Principle (OCP)](#openclosed-principle-ocp)
- [Liskov Substitution Principle (LSP)](#liskov-substitution-principle-lsp)
- [Interface Segregation Principle (ISP)](#interface-segregation-principle-isp)
- [Dependency Inversion Principle (DIP)](#dependency-inversion-principle-dip)

### Single Responsibility Principle (SRP)
У класса должна быть только одна причина для изменения.

Проблема:

```kotlin
fun main() {
    val otp1 = OTPSender()
    otp1.send("123456", "email")
}

class OTPSender() {
    fun send(otp: String, strategy: String) {
        if (isValid(otp, strategy)) {
            println("[S] Submitting OTP -> $otp")
        }
    }

    fun isValid(otp: String, validationType: String): Boolean {
        return when (validationType) {
            "email" -> {
                otp.length == 6 && otp.all { it.isDigit() }
            }
            "phone" -> {
                otp.length == 6 && otp.all { it.isDigit() } && otp.startsWith("123")
            }
            "bank" -> {
                otp.length == 8 && otp.all { it.isDigit() } && otp.startsWith("541")
            }
            else -> {
                false
            }
        }
    }
}
```

Решение:

```kotlin
fun main() {
    val otp2 = OTPSender(OTPValidator())
    otp2.send("123456", "email")
}

class OTPSender(private val otpValidator: OTPValidator) {
    fun send(otp: String, validationType: String) {
        if (otpValidator.isValid(otp, validationType)) {
            println("[S] Submitting OTP -> $otp")
        }
    }
}

class OTPValidator {
    fun isValid(otp: String, validationType: String): Boolean {
        return when (validationType) {
            "email" -> {
                otp.length == 6 && otp.all { it.isDigit() }
            }
            "phone" -> {
                otp.length == 6 && otp.all { it.isDigit() } && otp.startsWith("123")
            }
            "bank" -> {
                otp.length == 8 && otp.startsWith("123") && otp.all { it.isDigit() }
            }
            else -> {
                false
            }
        }
    }
}
```

### Open/Closed Principle (OCP)
Класс открыт для расширения, но закрыт для изменения. 

То есть добавляем новый функционал не меняя старый код.

Проблема:

```kotlin
fun main() {
    val otp1 = OTPSender(OTPValidator())
    otp1.send("123456", "email")
}

class OTPSender(private val otpValidator: OTPValidator) {
    fun send(otp: String, validationType: String) {
        if (otpValidator.isValid(otp, validationType)) {
            println("[O] Submitting OTP -> $otp")
        }
    }
}

class OTPValidator {
    fun isValid(otp: String, validationType: String): Boolean {
        return when (validationType) {
            "email" -> {
                otp.length == 6 && otp.all { it.isDigit() }
            }
            "phone" -> {
                otp.length == 6 && otp.all { it.isDigit() } && otp.startsWith("123")
            }
            "bank" -> {
                otp.length == 8 && otp.startsWith("123") && otp.all { it.isDigit() }
            }
            else -> {
                false
            }
        }
    }
}
```

Решение:

```kotlin
fun main() {
    val otp = OTPSender()
    otp.send("123456", OTPEmailValidator())
}

class OTPSender() {
    fun send(otp: String, otpValidator: OTPValidator) {
        if (otpValidator.isValid(otp)) {
            println("[O] Submitting OTP -> $otp")
        }
    }
}

interface OTPValidator {
    fun isValid(otp: String): Boolean
}

class OTPEmailValidator : OTPValidator {
    override fun isValid(otp: String): Boolean {
        return otp.length == 6 && otp.all { it.isDigit() }
    }
}

class OTPPhoneValidator : OTPValidator {
    override fun isValid(otp: String): Boolean {
        return otp.length == 6 && otp.all { it.isDigit() } && otp.startsWith("123")
    }
}

class OTPBankValidator : OTPValidator {
    override fun isValid(otp: String): Boolean {
        return otp.length == 8 && otp.startsWith("123") && otp.all { it.isDigit() }
    }
}
```

### Liskov Substitution Principle (LSP)
Наследники должны корректно заменять базовый класс

Проблема:

```kotlin
fun main() {
    val eagle = Eagle()
    val duck = Duck()
    val penguin = Penguin()

    makeBirdFly(eagle)      // Flying super high!
    makeBirdFly(duck)       // Flying low!
    makeBirdFly(penguin)    // Exception: Penguins can't fly!
}

// Function that expects a Bird and calls fly
fun makeBirdFly(bird: Bird) {
    bird.fly()
}

open class Bird {
    open fun walk() {
        println("[L] Walking!")
    }

    open fun fly() {
        println("[L] Flying!")
    }
}

class Eagle : Bird() {
    override fun fly() {
        println("[L] Flying super high!")
    }
}

class Duck : Bird() {
    override fun walk() {
        println("[L] Duck is walking slow")
    }

    override fun fly() {
        println("[L] Duck is Flying low")
    }
}

class Penguin : Bird() {
    override fun walk() {
        println("[L] Penguin is walking funny")
    }

    override fun fly() {
        throw UnsupportedOperationException("[L] Penguins can't fly!")
    }
}
```

Решение:

```kotlin
fun main() {
    val eagle = Eagle()
    val duck = Duck()
    val penguin = Penguin()

    makeBirdFly(eagle)      // Flying super high!
    makeBirdFly(duck)       // Flying low!
    //makeBirdFly(penguin)    // code doesn't allow us to define this line
}

// Function that expects a Flyable bird and calls fly
fun makeBirdFly(flyable: Flyable) {
    flyable.fly()
}

// Interface for flying behavior
interface Flyable {
    fun fly() {
        println("[L] Flying!")
    }
}

// Base class
open class Bird {
    open fun walk() {
        println("[L] Walking!")
    }
}

// Subclass: Eagle, which can fly
class Eagle : Bird(), Flyable {
    override fun fly() {
        println("[L] Flying super high!")
    }
}

// Subclass: Duck, which can fly
class Duck : Bird(), Flyable {
    override fun walk() {
        println("[L] Duck is walking slow")
    }

    override fun fly() {
        println("[L] Duck is Flying low")
    }
}

// Subclass: Penguin, which cannot fly
class Penguin : Bird() {
    override fun walk() {
        println("[L] Penguin is walking funny")
    }
}
```

### Interface Segregation Principle (ISP)
Лучше много маленьких интерфейсов, чем один большой

Проблема:

```kotlin
fun main() {
    val eagle = Eagle()
    val duck = Duck()
    val penguin = Penguin()

    makeFlyableFly(eagle)       // Flying super high!
    makeFlyableFly(duck)        // Flying low
    // makeFlyableFly(penguin)  // Empty implementation, does nothing or throws exception

    // makeSwimmableSwim(eagle) // Empty implementation, does nothing or throws exception
    makeSwimmableSwim(duck)     // swim slow
    makeSwimmableSwim(penguin)  // Swimming fast!
}

// Interface for bird actions
interface BirdActions {
    fun fly() {}
    fun swim() {}
}

// Base class
open class Bird

// Subclass: Sparrow, which can fly but forced to implement swim
class Eagle : Bird(), BirdActions {
    override fun fly() {
        println("[I] Flying super high!")
    }

    override fun swim() { }
}

// Subclass: Penguin, which can swim but forced to implement fly
class Penguin : Bird(), BirdActions {
    override fun fly() { }

    override fun swim() {
        println("[I] Swimming fast!")
    }
}

class Duck : Bird(), BirdActions {
    override fun fly() {
        println("[I] Flying low!")
    }

    override fun swim() {
        println("[I] Swimming slow!")
    }
}

// Function that expects a BirdActions object and calls fly
fun makeFlyableFly(birdActions: BirdActions) {
    birdActions.fly()
}

// Function that expects a BirdActions object and calls swim
fun makeSwimmableSwim(birdActions: BirdActions) {
    birdActions.swim()
}
```

Решение:

```kotlin
fun main() {
    val eagle = Eagle()
    val penguin = Penguin()
    val duck = Duck()

    makeFlyableFly(eagle)        // Flying super high!
    makeFlyableFly(duck)         // Flying low
    // makeFlyableFly(penguin)   // Compilation error: Penguin does not implement Flyable

    // makeSwimmableSwim(eagle)  // Compilation error: Sparrow does not implement Swimmable
    makeSwimmableSwim(duck)      // Swimming slow
    makeSwimmableSwim(penguin)   // Swimming fast!
}

// Interface for flyable actions
interface Flyable {
    fun fly()
}

// Interface for swimmable actions
interface Swimmable {
    fun swim()
}

// Base class
open class Bird

// Subclass: Sparrow, which can fly
class Eagle : Bird(), Flyable {
    override fun fly() {
        println("Flying super high!")
    }
}

// Subclass: Penguin, which can swim
class Penguin : Bird(), Swimmable {
    override fun swim() {
        println("Swimming fast!")
    }
}

// Subclass: Duck, which can both fly and swim
class Duck : Bird(), Flyable, Swimmable {
    override fun fly() {
        println("Flying low!")
    }

    override fun swim() {
        println("Swimming slow!")
    }
}

// Function that expects a Flyable object and calls fly
fun makeFlyableFly(flyable: Flyable) {
    flyable.fly()
}

// Function that expects a Swimmable object and calls swim
fun makeSwimmableSwim(swimmable: Swimmable) {
    swimmable.swim()
}
```

### Dependency Inversion Principle (DIP)
Зависим от абстракций, а не от реализаций

Проблема:

```kotlin
fun main() {
    val paymentProcessor = PaymentProcessor(PayPalPayment())
    paymentProcessor.processPayment(100.0)
}

class PaymentProcessor(private val payment: PayPalPayment) {
    fun processPayment(amount: Double) {
        payment.pay(amount)
    }
}

class PayPalPayment {
    fun pay(amount: Double) {
        println("[D] Processing PayPal payment of $$amount")
    }
}
```

Решение:

```kotlin
fun main() {
    val paymentProcessor1 = PaymentProcessor(PayPalPayment())
    paymentProcessor1.processPayment(100.0)

    val paymentProcessor2 = PaymentProcessor(StripePayment())
    paymentProcessor2.processPayment(200.0)
}

class PaymentProcessor(private var paymentMethod: Payment) {
    fun processPayment(amount: Double) {
        paymentMethod.pay(amount)
    }
}

interface Payment {
    fun pay(amount: Double)
}

class PayPalPayment : Payment {
    override fun pay(amount: Double) {
        println("[D] Processing PayPal payment of $$amount")
    }
}

class StripePayment : Payment {
    override fun pay(amount: Double) {
        println("[D] Processing Stripe payment of $$amount")
    }
}
```