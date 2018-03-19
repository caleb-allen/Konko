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
class Flow<T, U>(private val inChannel : ReceiveChannel<T>, operator: IntermediateOperator<T, U>) {
    private val outChannel: Channel<U> = Channel(500)

    init {
        operator.downstream = outChannel
        launch {
            val jobs = mutableListOf<Job>()
            for (item in inChannel) {
                jobs.add(launch {
                    operator.operate(item)
                })
            }
            jobs.forEach { it.join() }
            operator.downstream.close()
        }
    }

    fun forEach(block: (U) -> Unit): TerminalFlow<U> {
        return TerminalFlow(outChannel, ForEachOperator(block))
    }

    fun <V> map(mapper: (U) -> V): Flow<U, V> {
        val mapOperator = MapOperator(mapper)
        return Flow(outChannel, mapOperator)
    }

    fun filter(filter: (U) -> Boolean) : Flow<U, U>{
        return Flow(outChannel, FilterOperator(filter))
    }
    companion object {
        fun <T> from(receiveChannel: ReceiveChannel<T>): Flow<T, T>{
            return Flow(receiveChannel, PassthroughOperator())
        }

        fun <T> from(file: File) {

        }
    }
}

class TerminalFlow<T>(private val inChannel: ReceiveChannel<T>, operator: Operator<T>) {
    init {
        runBlocking {
            for (item in inChannel) {
                operator.operate(item)
            }
        }
    }
}