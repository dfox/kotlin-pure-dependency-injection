import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun measureDuration(code: () -> Unit): Double {
    return measureTime(code).toDouble(DurationUnit.MILLISECONDS)
}

@OptIn(ExperimentalTime::class)
fun measureDuration(msg : String, code: () -> Unit): Double {
    val duration = measureDuration(code)
    println("$msg in $duration ms")
    return duration
}