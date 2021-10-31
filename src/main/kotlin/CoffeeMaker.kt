
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