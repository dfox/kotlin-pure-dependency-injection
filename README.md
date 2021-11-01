# Compile Time Dependency Injection with Pure Kotlin

There are now several Dependency Injection frameworks for Kotlin which utilize a more functional approach with constructor injection, such as [Koin](https://insert-koin.io), [Dagger](https://dagger.dev), and others. These frameworks provide very flexible, non-invasive dependency injection. They also have very clean APIs utilizing annotations, or the DSL features of Kotlin. 

However, Koin still processes dependencies at runtime. This means that when the wiring code runs, it will fail at runtime if all the dependencies have not been correctly wired up. Dagger is a compile-time framework, but it requires a compiler plugin. 

Using Kotlin's delegate functionality and some simple patterns, it's possible to get clean code without the boilerplate of pure constructor injection, while still getting compile-time resolution, in native Kotlin, with no additional libraries, compiler plugins, or reflection overhead.

## Kotlin Delegates

The key to this pattern is Kotlin's delegate functionality. Like Java, Kotlin does not have multiple inheritance. It does allow classes to implement multiple interfaces though, and by using delegation, can implement much of the same functionality. 

Using Koin's CoffeeMaker example, create a `CoffeeMaker` class which takes, as a single argument, a `CoffeeMakerConfig`. The `CoffeeMakerConfig` interface describes the dependencies which `CoffeeMaker` requires. `CoffeeMaker` also implements this interface by delegating to the constructor argument, allowing the dependencies to be accessed as properties directly. This makes the pattern as clean as constructor injection.

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
    config: ThermosiphonConfig
) : ThermosiphonConfig by config, Pump {

    override fun pump() {
        if (heater.isHot()) {
            println("=> => pumping => =>")
        }
    }
}
```

## Wiring

The configurations are interfaces, so they can be implemented by a single application class. The application class contains the dependencies for both interfaces, and can be passed to the individual dependencies without the boilerplate of having to repeat the constructor arguments for each dependency. 

```kotlin
class CoffeeApp : CoffeeMakerConfig, ThermosiphonConfig  {
    val maker = CoffeeMaker(this)
    override val heater: Heater = ElectricHeater()
    override val pump: Pump = Thermosiphon(this)
}
```

## Common Dependencies

When wiring an application, there will be many shared dependencies. As long as the types in the interfaces line up, these dependencies will be shared automatically, provided they are defined in the class implementing the interfaces. It's also possible to pull common dependencies into a shared class which can be delegated to:

```kotlin
interface CommonConfig : CoffeeMakerConfig, ThermosiphonConfig

class Common : CommonConfig  {
    override val heater: Heater = ElectricHeater()
    override val pump: Pump = Thermosiphon(this)
}

class CoffeeApp : Common by Common() {
    val maker = CoffeeMaker(this)
}
```

or with a shared instance:

```kotlin
val common = Common()

class CoffeeApp : Common by common {
    val maker = CoffeeMaker(this)
}
```

## Types vs Names

You will notice that unlike pure constructor injection and most DI frameworks, this strategy differentiates dependencies by both type *and* name. The configurations must have the same name for the same type. It seems easy to get name clashes. In practice, it is no different from type clashes with a type-based DI framework. Usually, one names their dependencies the same as the type itself. For example, naming the variable holding an instance of `Heater` as `heater`. So in most cases, this will simply suggest more consistency. Should you need to alias a type, then do so. Nothing is preventing any of the usual strategies one uses for a program including falling back to pure constructor injection.  

## Singletons vs Factories

Because we are using simple Kotlin code, define factories and singletons using variables and functions. There is no need for additional abstractions.

## Scopes

Constructor injection is still the core pattern, so it's possible to create instances of other configurations and inject those, should there be name clashes or other reasons for needing scopes.

## Retrofitting 

If there are classes which use pure constructor injection, you can instantiate those as you would normally, or create a module to encapsulate them:

```kotlin
class CoffeeMaker(
    val pump: Pump,
    val heater: Heater
) {

    fun brew() {
        heater.on()
        pump.pump()
        println(" [_]P coffee! [_]P ")
        heater.off()
    }
}

class CoffeeMakerModule(common: Common) : Common by common {
    val maker = CoffeeMaker(pump, heater)
}

class CoffeeApp : Common by Common() {
    val maker = CoffeeMakerModule(this).maker
}
```











