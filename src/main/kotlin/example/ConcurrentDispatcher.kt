package example

import old.Dispatcher
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.util.*

//TODO maybe use a factory for the consumers so that we can spin them up on demand and allocate sections of the
// (immutable?) queue. Maybe each dispatcher will have to create other, smaller dispatchers until ya get real right
// small
class ConcurrentDispatcher<T>(private val min: Long = 4) : Dispatcher<T>() {
    private val itemQueue = LinkedList<T>()
    override fun onNext(item: T) {
        itemQueue.add(item)

        if (itemQueue.size >= min) {
            flushItems()
        }
    }

    override fun onComplete() {
        flushItems()
        super.onComplete()
    }

    private fun flushItems(){
        var consumerIndex = 0
        val consumerSize = consumers.size
        runBlocking {
            itemQueue.forEach {item ->
                val i = consumerIndex
                launch {
                    consumers[i].onNext(item)
                }
                consumerIndex++
                if (consumerIndex == consumerSize) {
                    consumerIndex = 0
                }
            }

        }
    }
}