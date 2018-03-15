package flow

import kotlinx.coroutines.experimental.channels.*
import kotlinx.coroutines.experimental.runBlocking


class Flow<in T, U>(
        private val upstreamChannel: ReceiveChannel<T>, private val oper: IntermediateOperator<T, U>):
        ReceiveChannel<U> by operatorDelegate(oper, upstreamChannel){

    fun <V> map(mapper: (U) -> V): Flow<U, V> {
        return Flow(this, StatelessOperator(mapper))
    }

    fun forEach(block: (U) -> Unit) {
        runBlocking {
            while (!isClosedForReceive) {
                block(receive())
            }
        }
    }
    companion object {
        fun <T> just(iterable: Iterable<T>): Flow<T, T>{
            return Flow(iterable.asReceiveChannel(), PassthroughOperator())
        }
    }
}

// provides
fun <T, U> operatorDelegate(oper: IntermediateOperator<T, U>, upstreamChannel: ReceiveChannel<T>):ReceiveChannel<U> {
    return produce(block = {
        send(oper.apply(upstreamChannel))
    })
}