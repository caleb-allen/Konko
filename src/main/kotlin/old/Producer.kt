package old

import example.SimpleDispatcher

abstract class Producer<T> {
    protected val dispatcher: Dispatcher<T> = SimpleDispatcher()

    internal fun subscribe(consumer: Consumer<T>) {
        dispatcher.addConsumer(consumer)
    }

    abstract fun getNext(howMany: Long)
}