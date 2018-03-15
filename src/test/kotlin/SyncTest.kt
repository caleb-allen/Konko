import flow.Flow
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test

class SyncTest {
    @Test fun testFlowWithChannels() = runBlocking{
        val a = listOf(0, 1, 2, 3, 4)

        Flow.just(a)
                .map { it + 5 }
                .forEach { println(it) }
        delay(10000)
        println("Done")
    }
}