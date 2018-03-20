package channel

import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.ReceiveChannel

abstract class StatelessOperator<T, U> : Flow<U> {
    override val downstream: Channel<U> = Channel(Channel.UNLIMITED)
    abstract val dispatcher: Dispatcher<T, U>
}

class BaseOperator<T, U>(upstream: ReceiveChannel<T>, operation: Operation<T, U>): StatelessOperator<T, U>(){
    override val downstream: Channel<U> = Channel(Channel.UNLIMITED)
    override val dispatcher =
            if (operation.stateful){
                SynchronousDispatcher(upstream, operation, downstream)
            }else{
                ConcurrentDispatcher(upstream, operation, downstream)
            }

    init {
        dispatcher.run()
    }
}

class MapOperation<in T, U>(private val mapper: (T) -> U) : Operation<T, U>() {
    override val stateful: Boolean = false
    override suspend fun apply(item: T) {
        send(mapper(item))
    }
}

class FilterOperation<T>(private val filter: (T) -> Boolean) : Operation<T, T>() {
    override val stateful: Boolean = false
    override suspend fun apply(item: T) {
        if (filter(item)) {
            send(item)
        }
    }
}

class LimitOperation<T>(private val limit: Long) : Operation<T, T>() {
    override val stateful: Boolean = true
    private var itemsApplied = 0
    override suspend fun apply(item: T) {
        if (itemsApplied < limit) {
            send(item)
            itemsApplied++
        }else{
            done()
        }
    }
}

class FlatMapOperation<T, U>(private val mapper: (T) -> Collection<U>) : Operation<T, U>() {
    override val stateful: Boolean = false
    override suspend fun apply(item: T) {
        mapper(item).forEach { send(it) }
    }
}

abstract class Operation<in T, U>{
    abstract val stateful: Boolean
    abstract suspend fun apply(item: T)
    lateinit var send: suspend (U) -> Unit
    lateinit var done: suspend () -> Unit
}