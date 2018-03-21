package channel

import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.asReceiveChannel
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.io.BufferedReader
import java.io.File
import java.io.FileReader


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
interface Flow<T> {
    val downstreams : List<ReceiveChannel<T>>

    companion object {
        fun <T> from(receiveChannel: ReceiveChannel<T>): Flow<T>{
            return BaseFlow(receiveChannel)
        }

        fun <T> from(collection: Collection<T>): Flow<T>{
            /*val collectionChannel = Channel<T>(collection.size)

            launch {
                collection.forEach {
                    collectionChannel.send(it)
                }
                collectionChannel.close()
            }*/

            return BaseFlow(collection.asReceiveChannel())
        }

        fun from(file: File) : Flow<String> {
            val br = BufferedReader(FileReader(file))
            val fileChannel = Channel<String>(Channel.UNLIMITED)
            launch {
                var s : String? = br.readLine()
                while (s != null) {
                    fileChannel.send(s)
                    s = br.readLine()
                }
                println("Done reading file")
                br.close()
                fileChannel.close()
            }
            return BaseFlow(fileChannel)
        }
    }

    fun <U> map(mapper: (T) -> U): Flow<U> {
        return buildFlow(MapOperation(mapper))
    }

    fun filter(filter: (T) -> Boolean): Flow<T> {
        return buildFlow(FilterOperation(filter))
    }

    fun limit(limit: Long): Flow<T>{
        return buildFlow(LimitOperation(limit))
    }

    fun <U> flatMap(mapper: (T) -> Collection<U>): Flow<U> {
        return buildFlow(FlatMapOperation(mapper))
    }

    private fun <U> buildFlow(operation: Operation<T, U>): Flow<U>{
        return Operator(downstreams, operation)
    }

    suspend fun consumeEach(block: suspend (T) -> Unit){
        for (stream in downstreams) {
            launch {
                for (item in stream) {
                    block(item)
                }
            }
        }
    }

    fun forEach(block: (T) -> Unit) {
        runBlocking {
            for (stream in downstreams) {
                for (item in stream) {
                    block(item)
                }
            }
        }
    }

    fun count(): Int{
        var count = 0
        runBlocking {
            for (stream in downstreams) {
                for (item in stream) {
                    count++
                }
            }
        }
        return count
    }
}

class BaseFlow<T>(downstream: ReceiveChannel<T>): Flow<T> {
    override val downstreams = listOf(downstream)
}
