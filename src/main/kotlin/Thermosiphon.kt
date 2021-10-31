
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