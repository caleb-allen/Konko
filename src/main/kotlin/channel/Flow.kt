package channel

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.*
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.io.File

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
abstract class Flow<out T> {
    protected abstract val stateful: Boolean
    protected abstract val channel : ReceiveChannel<T>

    companion object {
        fun <T> from(receiveChannel: ReceiveChannel<T>): Flow<T>{
            return BaseFlow(receiveChannel)
        }

        fun <T> from(file: File) {

        }
    }

    fun <V> map(mapper: (T) -> V): Flow<V> {
        return buildFlow(MapOperation(mapper))
    }

    fun filter(filter: (T) -> Boolean): Flow<T> {
        return buildFlow(FilterOperation(filter))
    }

    private fun <U> buildFlow(operation: Operation<T, U>): Flow<U>{
        return BaseOperator(channel, operation)
    }

    fun forEach(block: (T) -> Unit) {
        runBlocking {
            for (item in channel) {
                block(item)
            }
        }
    }
}

class BaseFlow<out T>(override val channel: ReceiveChannel<T>): Flow<T>(){
    override val stateful: Boolean = true
}