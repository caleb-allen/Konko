package channel

import kotlinx.coroutines.experimental.channels.*
import kotlinx.coroutines.experimental.runBlocking

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
class Flow<out T>(operator: IntermediateOperator<*, T>) {
    private val outChannel: Channel<T> = Channel(Channel.UNLIMITED)

    init {
        operator.run(outChannel)
    }

//    constructor(channel: Channel<U>){
//        outChannel = channel
//    }

//
    fun forEach(action: (T) -> Unit) = runBlocking {
        for (item in outChannel) {
            action(item)
        }
    }

    fun <U> map(mapper: (T) -> U): Flow<U> {
        val mapOperator = MapOperator(outChannel, mapper)
        return Flow(mapOperator)
    }

    companion object {

        fun <T> from(receiveChannel: ReceiveChannel<T>): Flow<T>{
            return Flow(PassthroughOperator(receiveChannel))
        }
    }

}