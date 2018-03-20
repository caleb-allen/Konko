package channel

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.*
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

interface Dispatcher<T, U>{
    val upstream: ReceiveChannel<T>
    val operation: Operation<T, U>
    val downstream: SendChannel<U>
    fun run()
}

class SynchronousDispatcher<T, U>(
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
}

class ConcurrentDispatcher<T, U>(override val upstream: ReceiveChannel<T>,
                                 override val operation: Operation<T, U>,
                                 override val downstream: SendChannel<U>): Dispatcher<T, U> {

    private val concurrentMax = 10

    override fun run() {
        println("Starting dispatcher")
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
                            val t: Long = measureNanoTime {
                                operation.apply(item)
                            }
                            operationTimes.send(it.toInt())
                        }
                        operationTimes.close()
                        println("Job is done")
                    }
                }
                println("It took ${jobsLaunched - startJob}ms to start the jobs")
                jobs.forEach {
                    it.join()
                    it.getCancellationException()
                }
                println("Job size: ${jobs.size}")
            }

            println("Jobs are done")
            println("Jobs took $time")
//            println(operationTimes)
            val times = operationTimes.toList()
            println(times)
            val avg = times.sum() / times.size
            println("Total: ${times.sum()}")
            println("Operations took ${avg / 1000} on average")
            downstream.close()

        }
    }
}