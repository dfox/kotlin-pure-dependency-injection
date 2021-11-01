
class CoffeeApp : CoffeeMakerConfig, ThermosiphonConfig {
    override val heater = ElectricHeater()
    override val pump = Thermosiphon(this)
    val maker = CoffeeMaker(this)
}

fun main() {
    val coffeeShop = CoffeeApp()

    measureDuration("Got Coffee") {
        coffeeShop.maker.brew()
    }
}

