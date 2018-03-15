import flow.Flow
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test

class SyncTest {
    @Test fun testFlowWithChannels() = runBlocking{
//        val a = listOf(0, 1, 2, 3, 4)
        val a = produce {
            var i = 0
            while (true) {
                println("Sending $i")
                send(i++)
            }
        }
        Flow.just(a)
                .map {
                   runBlocking { delay(1000) }
                    it * it
                }
                .forEach { println(it) }
        println("Done")
    }
}