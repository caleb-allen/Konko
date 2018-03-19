package channel

import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.runBlocking

abstract class StatelessOperator<T, U> : Flow<U> {
    override val stateful: Boolean = false
    override val downstream: Channel<U> = Channel(Channel.UNLIMITED)
    abstract val dispatcher: Dispatcher<T, U>
}

class BaseOperator<T, U>(upstream: ReceiveChannel<T>, operation: Operation<T, U>): StatelessOperator<T, U>(){
    override val downstream: Channel<U> = Channel(3)
    override val dispatcher = ConcurrentDispatcher(upstream, operation, downstream)
    init {
        dispatcher.run()
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
    lateinit var send: suspend (U) -> Unit
}