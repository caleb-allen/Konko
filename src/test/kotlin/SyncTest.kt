import flow.BaseOperator
import flow.Flow
import flow.Operator
import kotlinx.coroutines.experimental.channels.asReceiveChannel
import kotlinx.coroutines.experimental.channels.consume
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test

class SyncTest {
    @Test fun testFlowWithChannels() = runBlocking{
        val a = listOf(0, 1, 2, 3, 4)

        Flow(a.asReceiveChannel(), BaseOperator<Int, String>({
            "$it says hello"
        })).downstreamChannel.consumeEach {
            println("consuming")
            println(it)
        }
        delay(10000)
        println("Done")

    }
}