package channel

import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.launch
import util.log

/**
 * Actions that the operation will take. Dispatcher is to bind these to downstream channels.
 *
 * We do this rather than giving operations direct access to downstream because
 * dispatchers may wish to use strategies which are not 1:1 to how many operation
 * instances or channels are present
 */
interface OperationActions<in T>{
    suspend fun send(item: T)
    suspend fun done()
}

interface Dispatcher<T, U> {
    val upstreams: List<ReceiveChannel<T>>
    val operation: Operation<T, U>
    val downstreams: List<Channel<U>>
    fun run()
}

class OneToOneDispatcher<T, U>(
        override val upstreams: List<ReceiveChannel<T>>,
        override val operation: Operation<T, U>) : Dispatcher<T, U> {
    override val downstreams: List<Channel<U>> = List(upstreams.size) {Channel<U>(Channel.UNLIMITED)}
    override fun run() {
        log("Dispatching 1:1 from ${upstreams.size} streams")
        upstreams.forEachIndexed { index, upstream ->
            launch {
                val downstream = downstreams[index]
                val opActions = object : OperationActions<U> {
                    override suspend fun send(item: U) {
                        downstream.send(item)
                    }

                    override suspend fun done() {
                        downstream.close()
                    }

                }

                for (item in upstream) {
                    operation.apply(item, opActions)
                }
                downstream.close()
            }
        }
    }
}

class OneToManyDispatcher<T, U>(
        override val upstreams: List<ReceiveChannel<T>>,
        override val operation: Operation<T, U>) : Dispatcher<T, U> {
    override val downstreams: List<Channel<U>> = List(8) {Channel<U>(Channel.UNLIMITED)}
    override fun run() {
        if (upstreams.size != 1) {
            throw IllegalStateException("Requires 1 upstream channel." +
                    "Got ${upstreams.size}")
        }
        log("Dispatching from 1 to ${downstreams.size}")
        launch {
            val upstream = upstreams.first()

            val opActions = object : OperationActions<U> {
                var downstreamIndex = 0
                override suspend fun send(item: U) {
                    downstreams[downstreamIndex].send(item)
                }

                override suspend fun done() {
                    downstreams.forEach {
                        it.close()
                    }
                }
            }

            for (item in upstream) {
                operation.apply(item, opActions)
            }

            downstreams.forEach {
                it.close()
            }
        }
    }
}

class ManyToOneDispatcher<T, U>(
        override val upstreams: List<ReceiveChannel<T>>,
        override val operation: Operation<T, U>) : Dispatcher<T, U> {
    override val downstreams: List<Channel<U>> = List(1) {Channel<U>(Channel.UNLIMITED)}
    override fun run() {
        if (upstreams.size <= 1) {
            throw IllegalStateException("Requires more than 1 upstream channel." +
                    "Got ${upstreams.size}")
        }
        log("Dispatching from ${upstreams.size} to 1")
        launch {
            val opActions = object : OperationActions<U> {
                override suspend fun send(item: U) {
                    downstreams.first().send(item)
                }

                override suspend fun done() {
                    downstreams.first().close()
                }

            }

            val jobs = List(upstreams.size) {
                launch {
                    val upstream = upstreams[it]
                    for (item in upstream) {
                        operation.apply(item, opActions)
                    }
                }
            }

            jobs.forEach { it.join() }
            downstreams.first().close()
        }
    }
}


/*class SynchronousDispatcher<T, U>(
        override val upstream: ReceiveChannel<T>,
        override val operation: Operation<T, U>,
        override val downstream: SendChannel<U>): Dispatcher<T, U>{
    override fun run() {
        //todo pass the downstream in and have [Operation] map this stuff
        operation.send = { downstream.send(it) }
        operation.done = { downstream.close() }
        runBlocking {
            for (item in upstream) {
                operation.apply(item)
            }
            downstream.close()
        }
    }
}*/

/*
class ConcurrentDispatcher<T, U>(override val upstream: ReceiveChannel<T>,
                                 override val operation: Operation<T, U>,
                                 override val downstream: SendChannel<U>): Dispatcher<T, U> {

    private val concurrentMax = 10

    override fun run() {
        log("Starting dispatcher")
        operation.send = {downstream.send(it)}
        operation.done = {downstream.close()}


        launch {
            val operationTimes = Channel<Int>(Channel.UNLIMITED)
            val time = measureTimeMillis {
                val startJob = System.currentTimeMillis()
                val jobsLaunched = System.currentTimeMillis()
                val jobs = List(concurrentMax){
                    launch (coroutineContext){
                        for (item in upstream) {
                            val t: Long = measureTimeMillis {
                                operation.apply(item)
                            }
                            operationTimes.send(t.toInt())
                        }
                        operationTimes.close()
//                        log("Job $it is done")
                    }
                }
//                log("It took ${jobsLaunched - startJob}ms to start the jobs")
                jobs.forEach {
                    it.join()
//                    it.getCancellationException()
                }
//                log("Job size: ${jobs.size}")
            }

//            log("Jobs are done")
//            log("Jobs took $time")
//            log(operationTimes)
//            val times = operationTimes.toList()
//            log(times)
//            val avg = times.sum() / times.size
//            log("Total: ${times.sum()}")
//            log("Operations took ${avg} on average")
            downstream.close()

        }
    }
}*/
