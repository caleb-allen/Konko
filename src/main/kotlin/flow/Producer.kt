package flow

abstract class Producer<T> {
    protected abstract val dispatcher: Dispatcher<T>

    fun subscribe(consumer: Consumer<T>) {
        dispatcher.addConsumer(consumer)
    }

    abstract fun getNext(howMany: Long)
}