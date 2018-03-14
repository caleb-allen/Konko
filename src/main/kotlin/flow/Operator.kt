package flow

interface Operator<in T, U> {
    fun apply(item: T)
    fun setAddItemListener(callback: (U) -> Unit)
}

class BaseOperator<in T, U>(val transform: (T) -> U): Operator<T, U>{
    lateinit var callback: (U) -> Unit
    override fun apply(item: T) {
        callback(transform(item))
    }

    override fun setAddItemListener(callback: (U) -> Unit) {
        this.callback = callback
    }

}