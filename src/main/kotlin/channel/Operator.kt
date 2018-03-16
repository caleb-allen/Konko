package channel

import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.launch

interface Operator {
    val isStateful: Boolean
}

/**
 * applies lambda transform expression to received stream.
 */
abstract class IntermediateOperator<in T, out U>(private val upstream: ReceiveChannel<T>): Operator{
    abstract val downstream: ReceiveChannel<U>
}

//
//private fun <T, U> combineFlowWithOperator(upstream: ReceiveChannel<T>, oper: StatelessOperator<T, U>): Flow<U>{
//    val newChannel =
//    return Flow(newChannel)
//}

open class TerminalOperator<in T> : Operator {
    override val isStateful: Boolean = true
}

open class StatelessOperator<in T, out U>(upstream: ReceiveChannel<T>, transform: (T) -> U): IntermediateOperator<T, U>(upstream){
    override val downstream: ReceiveChannel<U> = produce(block = {
        while (!upstream.isClosedForReceive) {
            val jobs = List(5) {
                launch {
                    send(transform(upstream.receive()))
                }
            }
            jobs.forEach { it.join() }

        }
        println("Upstream channel closed.")
    })

    override val isStateful: Boolean = false
}

//open class StatefulOperator<in T, out U>(transform: (T) -> U): IntermediateOperator<T, U>(transform){
//    override val isStateful: Boolean = true
//}


//class PassthroughOperator<T> : StatelessOperator<T, T>({ it })