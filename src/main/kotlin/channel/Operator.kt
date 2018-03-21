package channel

import kotlinx.coroutines.experimental.channels.ReceiveChannel

class Operator<T, U>(upstreams: List<ReceiveChannel<T>>, operation: Operation<T, U>): Flow<U> {
    private val dispatcher: Dispatcher<T, U> = chooseDispatcher(upstreams, operation)
    override val downstreams: List<ReceiveChannel<U>> = dispatcher.downstreams

    companion object {
        private fun <T, U> chooseDispatcher(upstreams: List<ReceiveChannel<T>>,
                                            operation: Operation<T, U>): Dispatcher<T, U> =
                when {
                    upstreams.size > 1 && operation.stateful -> ManyToOneDispatcher(upstreams, operation)
                    upstreams.size == 1 && !operation.stateful -> OneToManyDispatcher(upstreams, operation)
                    else -> OneToOneDispatcher(upstreams, operation)
                }
    }


    init {
        dispatcher.run()
    }
}

//TODO terminal operations
interface Operation<T, U> {
    val stateful: Boolean
    suspend fun apply(item: T, actions: OperationActions<U>)
}

class MapOperation<T, U>(private val mapper: (T) -> U) : Operation<T, U> {
    override val stateful: Boolean = false
    override suspend fun apply(item: T, actions: OperationActions<U>) {
        actions.send(mapper(item))
    }
}

class FilterOperation<T>(private val filter: (T) -> Boolean) : Operation<T, T> {
    override val stateful: Boolean = false
    override suspend fun apply(item: T, actions: OperationActions<T>) {
        if (filter(item)) {
            actions.send(item)
        }
    }
}

class LimitOperation<T>(private val limit: Long) : Operation<T, T> {
    override val stateful: Boolean = true
    private var itemsApplied = 0
    override suspend fun apply(item: T, actions: OperationActions<T>) {
        if (itemsApplied < limit) {
            actions.send(item)
            itemsApplied++
        } else {
            actions.done()
        }
    }
}

class FlatMapOperation<T, U>(private val mapper: (T) -> Collection<U>) : Operation<T, U> {
    override val stateful: Boolean = false
    override suspend fun apply(item: T, actions: OperationActions<U>) {
        mapper(item).forEach { actions.send(it) }
    }
}