package flow

import kotlinx.coroutines.experimental.channels.*
import kotlinx.coroutines.experimental.runBlocking

/**
 * TODO maybe combine operators and flow? see [java.util.stream.ReferencePipeline]
 * also see [java.util.stream.ReferencePipeline.StatelessOp]
 *
 * the [Flow] should have one generic type--the output, but perhaps the ops should subclass Flow,
 *
 * and then they have an <In> and <Out> types or something
 *
 *
 * TODO maybe set Flow as an interface instead, and Operator is where all the meat of it happens
 */
open class Flow<out T> internal constructor(
        // this channel should NOT be just the parent--should be parent combined with an operator
        upstreamChannel: ReceiveChannel<T>):
        ReceiveChannel<T> by upstreamChannel{

    fun <U> map(mapper: (T) -> U): Flow<U> {
        return Flow(StatelessOperator(this, mapper).downstream)
    }

    fun forEach(block: (T) -> Unit) {
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
        fun <T> just(iterable: Iterable<T>): Flow<T>{
            return Flow(iterable.asReceiveChannel())
        }

        fun <T> just(receiveChannel: ReceiveChannel<T>): Flow<T>{
            return Flow(receiveChannel)
        }
    }
}