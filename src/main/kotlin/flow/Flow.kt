package flow

import example.SimpleDispatcher
import java.util.*

class Flow<T> private constructor(private val source: Producer<T>): Producer<T>(), Consumer<T> {
    init {
        source.subscribe(this)
    }
    override val dispatcher: Dispatcher<T> = SimpleDispatcher()
    private val itemsToEmit : Queue<T> = LinkedList()
    private var howMany: Long = 0
    private var isSourceComplete = false

    override fun getNext(howMany: Long) {
        this.howMany += howMany
        source.getNext(howMany)
        emit()
    }

    private fun emit(){
        while (itemsToEmit.isNotEmpty() && howMany > 0) {
            dispatcher.onNext(itemsToEmit.poll())
            howMany--
        }
        if (isSourceComplete && howMany == 0L) {
            dispatcher.onComplete()
        }
    }

    fun <U> map(mapper :(T) -> U): Flow<U> {

    }

    /**
     * receiving data from upstream
     */
    override fun onNext(item: T) {
        itemsToEmit.offer(item)
        emit()
        println("Consumer: $item")
    }

    override fun onComplete() {
        println("Complete")
        isSourceComplete = true
    }

    companion object {
        fun <T> fromIterable(source: Iterable<T>): Flow<T> {
            val iterator = source.iterator()
            return Flow(object : Producer<T>(){
                override val dispatcher: Dispatcher<T> = SimpleDispatcher()

                override fun getNext(howMany: Long) {
                    for (i in 0..howMany) {
                        if (iterator.hasNext()) {
                            dispatcher.onNext(iterator.next())
                        }else{
                            dispatcher.onComplete()
                        }
                    }
                }

            })
        }
    }
}