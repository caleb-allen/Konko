import channel.Flow
import channel.MapOperation
import channel.StatelessOperator
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.delay
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

        val downstream = Channel<String>(Channel.UNLIMITED)

        val mapOperation = MapOperation<Int, String>({ "Hello $it" })
        val mapOperator = StatelessOperator(upstream, downstream, mapOperation)
        mapOperator.run()
//        mapOperator.
        for (item in downstream) {
            println(item)
        }
    }
}