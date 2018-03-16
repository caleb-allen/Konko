package channel

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
 * TODO maybe set Flow as an interface instead, and TerminalOperator is where all the meat of it happens
 */
class Flow/*<T, U>(operator: IntermediateOperator<T, U>): IntermediateOperator<T, U> by operator*/{



    /*fun <U> map(mapper: (T) -> U): Flow<U> {
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
    }*/
    companion object {
        fun <T, U> map(mapper: (T) -> U) {

        }

//        fun <T> from(iterable: Iterable<T>): Flow<T>{
//            return Flow(iterable.asReceiveChannel())
//        }
//
//        fun <T> from(receiveChannel: ReceiveChannel<T>): Flow<T>{
//            return Flow(receiveChannel)
//        }
    }
}