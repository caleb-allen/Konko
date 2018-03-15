import flow.Flow
import kotlinx.coroutines.experimental.channels.asReceiveChannel
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
                delay(400)
                send(i++)
            }
        }
//        val b = a.asReceiveChannel()

//        while (!b.isClosedForReceive) {
//            println(b.receive())
//        }
        println("Now with flow")
        Flow.just(a)
                .map {
                    it + 5
                }
                .forEach { println(it) }
//        delay(1000)
        println("Done")
    }
}