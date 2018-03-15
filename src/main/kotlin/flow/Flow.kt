package flow

import kotlinx.coroutines.experimental.channels.*
import kotlinx.coroutines.experimental.runBlocking


class Flow<in T, U>(
        upstreamChannel: ReceiveChannel<T>, oper: IntermediateOperator<T, U>):
        ReceiveChannel<U> by operatorDelegate(oper, upstreamChannel){

    fun <V> map(mapper: (U) -> V): Flow<U, V> {
        return Flow(this, StatelessOperator(mapper))
    }

    fun forEach(block: (U) -> Unit) {
        runBlocking {
            while (!isClosedForReceive) {
                try {
                    block(receive())
                } catch (e: ClosedReceiveChannelException) {
//                    e.printStackTrace()
                    println("Tried to receive objects; channel was closed")
                }
            }
            println("Channel closed.")
        }
    }
    companion object {
        fun <T> just(iterable: Iterable<T>): Flow<T, T>{
            return Flow(iterable.asReceiveChannel(), PassthroughOperator())
        }

        fun <T> just(receiveChannel: ReceiveChannel<T>): Flow<T, T>{
            return Flow(receiveChannel, PassthroughOperator())
        }
    }
}

// provides
fun <T, U> operatorDelegate(oper: IntermediateOperator<T, U>, upstreamChannel: ReceiveChannel<T>):ReceiveChannel<U> {
    return produce(block = {
        while (!upstreamChannel.isClosedForReceive) {
//            println("Requested from downstream")
            val result = oper.apply(upstreamChannel)
//            println("sending result to downstream: $result")
            send(result)
        }
        println("Upstream channel closed.")
    })
}

// provides
//fun <T, U> receiveChannelOperatorDelegate(oper: IntermediateOperator<T, U>, upstreamChannel: ReceiveChannel<T>):ReceiveChannel<U> {
//    produce(block = {
//        println("Requested from downstream")
//        val result = oper.apply(upstreamChannel)
//        println("Emitting result: $result")
//        send(result)
//    })
//
////    Cha
//}
//
