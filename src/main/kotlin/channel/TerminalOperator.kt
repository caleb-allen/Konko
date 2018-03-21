package channel

import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.launch

class ReduceOperator<T>(upstreams: List<ReceiveChannel<T>>, operator: BinaryOperator<T>) {
    init {
        ReduceDispatcher(upstreams, operator)
    }
}

interface BinaryOperator<T>{
    fun apply(item1: T, item2: T): T
}


class ReduceDispatcher<T>(
        val upstreams: List<ReceiveChannel<T>>,
        val operator: BinaryOperator<T>) {
    val downstreams: Channel<T> = Channel(Channel.UNLIMITED)
    fun run() {
        println("Reducing from ${upstreams.size} to 1")
        launch {
            val jobs = List(upstreams.size) {
                launch {
                    val upstream = upstreams[it]

                    //TODO grab 2 items from upstream, operate, push to downstream
                    for (item in upstream) {

                    }
                }
            }
        }
    }
}