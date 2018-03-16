import channel.FilterOperator
import channel.Flow
import channel.MapOperator
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

        val downstream = Channel<Int>(Channel.UNLIMITED)

        val filter = FilterOperator<Int>({ it % 2 == 0}, upstream, downstream)
        filter.run()

//        for (item in downstream) {
//            println("Downstream: $item")
//        }
        println("running mapper...")
        val evenDownerstream = Channel<String>(Channel.UNLIMITED)

        val map = MapOperator({"Hello $it"}, downstream, evenDownerstream)
        map.run()

        for (item in evenDownerstream) {
            println(item)
        }
    }

    @Test fun flowTest(){

    }
}