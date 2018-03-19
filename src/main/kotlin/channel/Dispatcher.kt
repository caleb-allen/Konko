package channel

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

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
        operation.send = {downstream.send(it)}
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
    override fun run() {
        println("Starting dispatcher")
        operation.send = {downstream.send(it)}
        launch {
            val jobs = mutableListOf<Job>()
            for (item in upstream) {
                jobs.add(launch {
                    operation.apply(item)
                })
            }

            launch {
                jobs.forEach { it.join() }
                downstream.close()
            }
        }
    }
}