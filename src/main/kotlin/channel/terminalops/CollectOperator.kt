package channel.terminalops

import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

/**
 * Performs a mutable reduction on the Flow.
 *
 * @param T the type of the items to be operated on
 * @param R the type of the result. This is often a collection of some type
 *
 */
class CollectOperator<T, R> (
        private val upstreams: List<ReceiveChannel<T>>,
        private val seedFactory: () -> R,
        private val accumulator: (R, T) -> Unit,
        private val reducer: (R, R) -> Unit) {

    fun run(): R = runBlocking {
        val downstreamResults = Channel<R>(Channel.UNLIMITED)
        val jobs = List(upstreams.size){index ->
            launch (coroutineContext) {
                val collection = seedFactory()
                for (item in upstreams[index]) {
                    accumulator(collection, item)
                }
                downstreamResults.send(collection)
            }
        }
        jobs.forEach { it.join() }
        downstreamResults.close()
        val finalCollection = seedFactory()
        downstreamResults.consumeEach {
            reducer(finalCollection, it)
        }

        return@runBlocking finalCollection
    }
}

interface Seed<out R> {
    fun generate(): R
}

/**
 *
 */
interface BiFunction<in T, in U>{
    fun consume(item1: T, item2: U)
}