//package flow
//
///**
// * SyncFlow implementation must have the same "in" and "out" types
// */
//class PassthroughOperator<T> : Operator<T, T> {
//    private lateinit var callback: (T) -> Unit
//    override fun apply(item: T) {
//        callback(item)
//    }
//
//    override fun setAddItemListener(callback: (T) -> Unit) {
//        this.callback = callback
//    }
//
//}