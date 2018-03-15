package flow

import kotlinx.coroutines.experimental.channels.*
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

/**
 * TODO maybe combine operators and flow? see [java.util.stream.ReferencePipeline]
 * also see [java.util.stream.ReferencePipeline.StatelessOp]
 *
 * the [Flow] should have one generic type--the output, but perhaps the ops should subclass Flow,
 *
 * and then they have an <In> and <Out> types or something
 */
class Flow<out T> private constructor(
        // this channel should NOT be just the parent--should be parent combined with an operator
        upstreamChannel: ReceiveChannel<T>):
        ReceiveChannel<T> by upstreamChannel{

    fun <U> map(mapper: (T) -> U): Flow<U> {
        return combineFlowWithOperator(this, StatelessOperator(mapper))
    }
//
//    fun <T> filter(filter: (T) -> Boolean): Flow<T> {
//        return combineFlowWithOperator(this, StatelessOperator { if(filter){} })
//    }

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

        private fun <T, U> combineFlowWithOperator(upstream: Flow<T>, oper: StatelessOperator<T, U>): Flow<U>{
            val newChannel = produce(block = {
                while (!upstream.isClosedForReceive) {
                    val jobs = List(5) {
                        launch {
                            send(oper.apply(upstream))
                        }
                    }
                    jobs.forEach { it.join() }

                }
                println("Upstream channel closed.")
            })
            return Flow(newChannel)
        }

        fun <T> just(iterable: Iterable<T>): Flow<T>{
            return Flow(iterable.asReceiveChannel())
        }

        fun <T> just(receiveChannel: ReceiveChannel<T>): Flow<T>{
            return Flow(receiveChannel)
        }
    }
}