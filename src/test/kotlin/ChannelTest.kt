import channel.*
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test

class ChannelTest {
    @Test fun testFlowWithChannels() = runBlocking{
        val upstream = produce(capacity = 20) {
            for (i in 0..10) {
                println("Sending $i")
                send(i)
            }
        }

        Flow.from(upstream)
                .map { "$it says hello!" }
                .forEach { println(it) }
    }

    @Test fun flowTest(){

    }
}