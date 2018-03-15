package flow

import kotlinx.coroutines.experimental.channels.*
import java.util.*


open class Flow<T, U>(
        private val upstreamChannel: ReceiveChannel<T>,
        private val oper: Operator<T, U>){

    val downstreamChannel = produce<U> (block = {
        val upstreamQueue: Queue<T> = LinkedList()
        for (i in 0 until oper.batchSize) {
            upstreamQueue.offer(upstreamChannel.receive())
        }
        val result = oper.apply(upstreamQueue)
        send(result)
    })
}
    //TODO adaptive flow types with different SendChannel implementations for backpressure