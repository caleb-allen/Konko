package channel

import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.runBlocking


//this and intermediate should become an interface that both inherit a common operator
abstract class TerminalOperator<T> {
    abstract val stateful: Boolean

    abstract val upstream: ReceiveChannel<T>

    fun run() {
        runBlocking {
            for (item in upstream) {
//                operate(item)
            }
        }
    }
//    abstract suspend fun operate(item: T)
//    abstract val operation: Operation<T>
}

abstract class IntermediateOperator<T, U> {
    abstract val stateful: Boolean
    abstract val upstream: ReceiveChannel<T>
    abstract val downstream: SendChannel<U>

    fun run() {
        runBlocking {
            for (item in upstream) {
                operation.operate(item, downstream)
            }
            downstream.close()
        }
    }

    //    abstract suspend fun operate(item: T, downstream: suspend (U) -> Any)
    abstract val operation: Operation<T, U>
}

class StatelessOperator<T, U> (
        override val upstream: ReceiveChannel<T>,
        override val downstream: SendChannel<U>,
        override val operation: Operation<T, U>
): IntermediateOperator<T, U>(){
    override val stateful: Boolean = false
}

//class MapOperator<T, U>(
//        val mapper: (T) -> U,
//        upstream: ReceiveChannel<T>,
//        downstream: SendChannel<U>
//) : StatelessOperator<T, U>(upstream, downstream) {
//    override suspend fun operate(item: T, downstream: suspend (U) -> Any) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//}

class MapOperation<T, U>(private val mapper: (T) -> U) : Operation<T, U> {
    override suspend fun operate(item: T, downstream: SendChannel<U>) {
        downstream.send(mapper(item))
    }

}

interface Operation<T, U> {
    suspend fun operate(item: T, downstream: SendChannel<U>)
}