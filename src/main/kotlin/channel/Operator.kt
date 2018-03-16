package channel

import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.runBlocking

/**
 * terminal operator
 */
internal interface Operator<T> {
    val stateful: Boolean
    val upstream: ReceiveChannel<T>

    fun run() {
        runBlocking {
            for (item in upstream) {
                operate(item)
            }
            done()
        }
    }

    fun done() {}
    suspend fun operate(item: T)
}

internal interface IntermediateOperator<T, U> : Operator<T> {
    val downstream: SendChannel<U>

    override fun done() {
        println("Closing downstream")
        downstream.close()
        super.done()
    }
}

abstract class StatelessOperator<T, U>(
        override val upstream: ReceiveChannel<T>,
        override val downstream: SendChannel<U>) : IntermediateOperator<T, U> {
    override val stateful: Boolean = false
}

class MapOperator<T, U>(
        private val mapper: (T) -> U,
        upstream: ReceiveChannel<T>,
        downstream: SendChannel<U>
) : StatelessOperator<T, U>(upstream, downstream) {
    override suspend fun operate(item: T) {
        downstream.send(mapper(item))
    }
}

class FilterOperator<T>(
        private val filter: suspend (T) -> Boolean,
        upstream: ReceiveChannel<T>,
        downstream: SendChannel<T>
) : StatelessOperator<T, T>(upstream, downstream) {
    override suspend fun operate(item: T) {
        if (filter(item)) {
            downstream.send(item)
        }
    }
}

/*class MapOperation<T, U>(private val mapper: (T) -> U) : IntermediateOperation<T, U> {
    override suspend fun operate(item: T, downstream: SendChannel<U>) {
        downstream.send(mapper(item))
    }
}*/

interface IntermediateOperation<T, U> {
    suspend fun operate(item: T, downstream: SendChannel<U>)
}

interface TerminalOperation<T> {
    suspend fun operate(item: T)
}