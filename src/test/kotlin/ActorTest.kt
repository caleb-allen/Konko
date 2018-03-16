import actors.Flow
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test

class ActorTest {
    @Test fun testActors() = runBlocking{


        val downstream = Channel<Int>(Channel.UNLIMITED)
        val upstream = Flow.filterActor(downstream, { it % 2 == 0})

        for (i in 0..10) {
            println("Sending $i upstream")
            upstream.send(i)
        }
        println("Closing upstream")
        upstream.close()
        println("Closing downstream")
        downstream.close()

        downstream.iterator()
        for (message in downstream) {
            println("got $message")
        }

        println("Done")

        actor<Int>{

        }

    }
}