#Kotlin Pure Compile Time Dependency Injection

There are now several Dependency Injection frameworks for Kotlin which utilize a more functional approach with constructor injection, such as Koin, Dagger, and others. These frameworks provide very flexible, non-invasive dependency injection. They also have very clean APIs utilizing the DSL features of Kotlin. However, they still process dependencies at runtime. This means that when the wiring code runs, it will fail at runtime if all the dependencies have not been correctly wired up. 

Using Kotlin's delegate functionality and some simple patterns, it's possible to get just as clean code without the boilerplate of pure constructor injection, while still getting compile-time resolution, all in native Kotlin with no additional libraries.

## Kotlin Delegates

The key to this pattern is Kotlin's delegate functionality. Like Java, Kotlin does not have multiple inheritance. It does allow classes to implement multiple interfaces, and by using delegation, it can get much of the same functionality. 

Using Koin's CoffeeMaker example, create a `CoffeeMaker` class which takes, as a single argument, a `CoffeeMakerConfig`. The `CoffeeMakerConfig` interface describes the dependencies which `CoffeeMaker` requires. `CoffeeMaker` also implements this interface by delegating to the constructor argument, allowing the dependencies to be accessed as properties directly. This makes the pattern as clean as constructor injection, as there is no extra code. 

```kotlin
interface CoffeeMakerConfig {
    val pump: Pump
    val heater: Heater
}

class CoffeeMaker(
    config: CoffeeMakerConfig
): CoffeeMakerConfig by config {

    fun brew() {
        heater.on()
        pump.pump()
        println(" [_]P coffee! [_]P ")
        heater.off()
    }
}
```

The `Thermosiphon` class works the same way:

```kotlin
interface ThermosiphonConfig {
    val heater: Heater
}

class Thermosiphon(
    private val config: ThermosiphonConfig
) : ThermosiphonConfig by config, Pump {

    override fun pump() {
        if (heater.isHot()) {
            println("=> => pumping => =>")
        }
    }
}
```

Thermosiphon is interesting because it takes an implementation-specific configuration and also implements an interface. 

## Wiring

The configurations are interfaces, so they can be composed into a single application class. The application class contains all the dependencies, and can be passed to the individual dependencies without the boilerplate of having to repeat the constructor arguments for each dependency. 

```kotlin
class CoffeeApp : CoffeeMakerConfig, ThermosiphonConfig  {
    val maker = CoffeeMaker(this)
    override val heater: Heater = ElectricHeater()
    override val pump: Pump = Thermosiphon(this)
}
```

## Overriding Dependencies

For different environments, we often want to override single dependencies. For these situations, we'll want to pull the common dependencies into another class. 

```kotlin
interface CommonConfig : CoffeeMakerConfig, ThermosiphonConfig

class CommonApp : CommonConfig {
    override val heater = ElectricHeater()
    override val pump = Thermosiphon(this)
}
```

Now, these dependencies can be overridden in a variety of ways. It can be overridden by making a subclass of `CommonApp`, or by using delegation and overriding in the dependency root:

```kotlin
class LoggingCoffeeApp(
    val commonApp: CommonConfig = CommonApp()
) : CommonConfig by commonApp {
    val maker = CoffeeMaker(this)
    
    override val pump = object : Pump {
        override fun pump() {
            println("Starting pump")
            commonApp.pump.pump()
            println("Stopping pump")
        }
    }
}
```

It is also possible to save a `CommonApp` instance in a variable and delegate to that from multiple graphs. 

## Singletons vs Factories

Because we are using simple Kotlin code, factories and singletons are simply defined using variables and functions. So there is no need for additional code to abstract them. 

## Scopes

Constructor injection is still the main pattern used, so it's possible to create instances of other configurations and inject those, should there be name clashes or other reasons for needing scopes.









