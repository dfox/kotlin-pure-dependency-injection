
interface CommonConfig : CoffeeMakerConfig, ThermosiphonConfig

class CommonApp : CommonConfig {
    override val heater = ElectricHeater()
    override val pump = Thermosiphon(this)
}

val commonApp = CommonApp()

class CoffeeApp : CommonConfig by commonApp {
    val maker = CoffeeMaker(this)
}

class LoggingCoffeeApp : CommonConfig by commonApp {
    val maker = CoffeeMaker(this)

    override val pump = object : Pump {
        override fun pump() {
            println("Starting pump")
            commonApp.pump.pump()
            println("Stopping pump")
        }
    }
}

fun main() {
    val coffeeShop = CoffeeApp()

    measureDuration("Got Coffee") {
        coffeeShop.maker.brew()
    }

    val loggingCoffeeShop = LoggingCoffeeApp()

    measureDuration("Logging Coffee") {
        loggingCoffeeShop.maker.brew()
    }
}

