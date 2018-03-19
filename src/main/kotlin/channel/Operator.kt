package channel

import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.runBlocking

/**
 * terminal operator
 */
abstract class Operator<T> {
    protected abstract val stateful: Boolean
//    internal lateinit var upstream: ReceiveChannel<T>


    protected open fun done() {}
    internal abstract suspend fun operate(item: T)
}

abstract class IntermediateOperator<T, U>: Operator<T>(){
    internal lateinit var downstream: SendChannel<U>

    override fun done() {
        super.done()
        downstream.close()
    }

    protected suspend fun send(item: U) {
        downstream.send(item)
    }
}

abstract class StatelessOperator<T, U> : IntermediateOperator<T, U>() {
    override val stateful: Boolean = false
}

class MapOperator<T, U>(private val mapper: (T) -> U) : StatelessOperator<T, U>() {
    override suspend fun operate(item: T) {
        send(mapper(item))
    }
}

//class FlatMapOperator<T, U>(private val mapper: (T) -> Flow<U>)

class PassthroughOperator<T>: StatelessOperator<T, T>(){
    override suspend fun operate(item: T) {
        send(item)
    }
}

class FilterOperator<T>(private val filter: (T) -> Boolean) : StatelessOperator<T, T>() {
    override suspend fun operate(item: T) {
        if (filter(item)) {
            send(item)
        }
    }
}

///////////////////////////Terminal Operators///////////////////////////////

class ForEachOperator<T>(private val block: (T) -> Unit) : Operator<T>() {
    override val stateful: Boolean = true
    override suspend fun operate(item: T) {
        block(item)
    }
}