package channel.terminalops

import kotlinx.coroutines.experimental.channels.*
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import util.log
import kotlin.coroutines.experimental.coroutineContext

class ReduceOperator<T>(
        private val upstreams: List<ReceiveChannel<T>>,
        private val reducer: (T, T) -> T) {
    fun run(): T = runBlocking {
        val downstreamResults = Channel<T>(Channel.UNLIMITED)
        val jobs = List(upstreams.size){index ->
            launch (coroutineContext) {
                downstreamResults.send(upstreams[index].reduce(reducer))
            }
        }
        jobs.forEach { it.join() }
        return@runBlocking downstreamResults.reduce(reducer)
    }


    /**
     * Applies reduce function to each item in a channel using a concurrent binary tree approach.
     *
     * Each level of the tree uses its own channel and coroutine.
     *
     * Take this pseudo code example using a sum reducer:
     *
     *
     * Source Channel:  2    3    5    4    3
     *                   \  /      \  /     |
     * First stage:        5        9       3
     *                        \   /         |
     * Second stage:            14          3
     *                              \      /
     * Final Stage:                    17
     * TODO evaluate performance of linear ReceiveChannel.reduce (goes left to right) vs reducing with binary tree
     */
    private suspend fun reduceConcurrent(upstream: ReceiveChannel<T>) {
//        log("Beginning reduce for channel $upstream")
        val downstream: Channel<T> = Channel(Channel.UNLIMITED)
        var isTerminalReduce = true
        while (!downstream.isClosedForSend) {
            val item1 = upstream.receiveOrNull()
            val item2 = upstream.receiveOrNull()

            when {
                // upstream is empty, we're done reducing
                item1 == null -> downstream.close()
                // we have 1 leftover item. Send it to the next stage.
                item2 == null -> {
                    downstream.send(item1)
                    downstream.close()
                }
                // normal operation
                else -> {
                    log("Operating on $item1 and $item2")
                    downstream.send(reducer(item1, item2))
                    // we know that this is not the last level. Start the next stage
                    if (isTerminalReduce) {
                        launch(coroutineContext){
                            reduceConcurrent(downstream)
                        }
                        isTerminalReduce = false
                    }
                }
            }
        }

        if (isTerminalReduce) {
            log("Reached last stage! Channel: $downstream")
            var finalItem : T? = null
            for (item in downstream) {
                if (finalItem != null) {
                    throw IllegalStateException("Multiple items found in final reduce stage.")
                }
                finalItem = item
                log("Last stage item: $item")
            }
        }
    }
}