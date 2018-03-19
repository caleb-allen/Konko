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

abstract class IntermediateOperator<T, U>{
    protected abstract val stateful: Boolean

    private lateinit var downstream: SendChannel<U>
    protected abstract val upstream: ReceiveChannel<T>

    internal fun run(down: SendChannel<U>){
        this.downstream = down
        runBlocking {
            for (item in upstream) {
                operate(item)
            }
        }
        downstream.close()
    }

    protected suspend fun send(item: U) {
        downstream.send(item)
    }

    abstract suspend fun operate(item: T)
}

abstract class StatelessOperator<T, U> : IntermediateOperator<T, U>() {
    override val stateful: Boolean = false
}

class MapOperator<T, U>(override val upstream: ReceiveChannel<T>, private val mapper: (T) -> U) : StatelessOperator<T, U>() {
    override suspend fun operate(item: T) {
        send(mapper(item))
    }
}

class PassthroughOperator<T>(override val upstream: ReceiveChannel<T>): StatelessOperator<T, T>(){
    override suspend fun operate(item: T) {
        send(item)
    }

}


//class FilterOperator<T>(
//        private val filter: (T) -> Boolean) : StatelessOperator<T, T>() {
//    override suspend fun operate(item: T, downstream: SendChannel<T>) {
//        if (filter(item)) {
//            downstream.send(item)
//        }
//    }
//}


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