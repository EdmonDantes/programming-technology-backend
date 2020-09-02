import java.net.URL
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlin.system.measureNanoTime

fun main() {

    var prev: Long? = null

    while (true) {
        val tmp = measureNanoTime {
            URL("http://doublegum.site:8080/all").openConnection().apply {
                doInput = true
                val tmp = getInputStream().readAllBytes()
            }
        }

        if (prev == null) {
            prev = tmp;
        }

        if (tmp / prev > 2) {
            print(TimeUnit.MILLISECONDS.convert(tmp, TimeUnit.NANOSECONDS).absoluteValue)
            prev = tmp
        }
    }


}