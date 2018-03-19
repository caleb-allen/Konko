import channel.*
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test
import kotlin.system.measureTimeMillis

class ChannelTest {
    @Test fun testFlowWithChannels() {
        val upstream = produce(capacity = Channel.UNLIMITED) {
            for (i in 0..10) {
                send(i)
            }
        }

        val time = measureTimeMillis {
            Flow.from(upstream)
                    .filter { it % 2 == 0 }
                    .map { "$it says hello" }
                    .forEach { println(it) }
        }

        println("Total time: $time ms")
    }

    @Test fun flowTest() = runBlocking{
//        val upstream = produce(capacity = 20) {
//            for (i in 0..10) {
//                println("Sending $i")
//                send(i)
//            }
//        }
//
//        val a = upstream.receive()
//        println("Received $a")
//
//        IntermediateOperator.from(upstream)
//                .map { println("Map received $it");"$it says hello!" }
//                .forEach { println(it) }
//        return@runBlocking
    }
}