package channel

import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.runBlocking

/**
 * terminal operator
 */
/*abstract class Operator<T> {
    protected abstract val stateful: Boolean
    internal lateinit var upstream: ReceiveChannel<T>

    fun run() {
        runBlocking {
            for (item in upstream) {
                operate(item)
            }
        }
    }

    protected open fun done() {}
    internal abstract suspend fun operate(item: T)
}*/

abstract class StatelessOperator<T, U> : Flow<U>() {
    abstract val upstream: ReceiveChannel<T>
    override val stateful: Boolean = false
    override val channel: Channel<U> = Channel(Channel.UNLIMITED)

    abstract val operation: Operation<T, U>

    fun run() {
        runBlocking {
            operation.downstream = channel
            for (item in upstream) {
                operation.apply(item)
            }
            channel.close()
        }
    }
}

class BaseOperator<T, U>(override val upstream: ReceiveChannel<T>, override val operation: Operation<T, U>): StatelessOperator<T, U>(){
    init {
        run()
    }
}

class MapOperation<in T, U>(private val mapper: (T) -> U) : Operation<T, U>() {
    override suspend fun apply(item: T) {
        send(mapper(item))
    }
}

class FilterOperation<T>(private val filter: (T) -> Boolean) : Operation<T, T>() {
    override suspend fun apply(item: T) {
        if (filter(item)) {
            send(item)
        }
    }
}

abstract class Operation<in T, U>{
    abstract suspend fun apply(item: T)
    internal lateinit var downstream: SendChannel<U>
    suspend fun send(item: U) {
        downstream.send(item)
    }
}