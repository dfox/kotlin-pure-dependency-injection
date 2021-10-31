
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