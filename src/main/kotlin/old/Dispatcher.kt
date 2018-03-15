package old

abstract class Dispatcher<T>{
    private var demand: Long = 0
    protected val consumers = mutableListOf<Consumer<T>>()
    fun addConsumer(subscription: Consumer<T>) {
        consumers.add(subscription)
    }

    abstract fun onNext(item: T)

    open fun onComplete(){
        consumers.forEach { it.onComplete() }
    }
}
