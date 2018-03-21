//package flow
//
//import java.util.*
//
//class SyncFlow<in T, U> private constructor(
//        private val source: Producer<T>,
//        private val operator: TerminalOperator<T, U>)
//    : Consumer<T>, Producer<U>() {
//
//    private val itemsToEmit : Queue<U> = LinkedList<U>()
//    private var demand: Long = 0
//    private var isSourceComplete = false
//    private var amIComplete = false
//
//    init {
//        source.subscribe(this)
//        operator.setAddItemListener {
//            itemsToEmit.offer(it)
//            emit()
//        }
//    }
//
//    override fun getNext(demand: Long) {
//        if (amIComplete) {
//            throw IllegalStateException("SyncFlow has been declared complete")
//        }
//        this.demand += demand
//        source.getNext(demand)
//        emit()
//    }
//
//    private fun emit(){
//        if (amIComplete) {
//            throw IllegalStateException("SyncFlow has been declared complete")
//        }
//        while (itemsToEmit.isNotEmpty() && demand > 0) {
//            dispatcher.onNext(itemsToEmit.poll())
//            demand--
//        }
//        // our source is complete, so we're not going to get any more to fill the demand
//        // we should keep going until the demand is zero
//        if (isSourceComplete && (itemsToEmit.isEmpty())) {
//            dispatcher.onComplete()
//            amIComplete = true
//        }
//    }
//
//    fun subscribe(onNextCallback: ((U) -> Unit)? = null, onCompleteCallback: (() -> Unit)? = null): SyncFlow<T, U> {
//        dispatcher.addConsumer(object : Consumer<U> {
//            override fun onNext(item: U) {
//                onNextCallback?.let { it(item) }
//            }
//
//            override fun onComplete() {
//                onCompleteCallback?.let { it() }
//            }
//        })
//        return this
//    }
//
//    /**
//     * receiving data from upstream
//     */
//    override fun onNext(item: T) {
//        if (isSourceComplete) {
//            throw IllegalStateException("Source has been declared complete but has emitted additional items")
//        }
//        operator.apply(item)
//    }
//
//    override fun onComplete() {
////        println("${this::class.java.name}: Complete")
//        isSourceComplete = true
////        emit()
//    }
//
//    fun <V> map(transform: (U) -> V): SyncFlow<U, V> {
//        return SyncFlow(this, Operator(transform))
//    }
//
//    companion object {
//        fun <T> fromIterable(source: Iterable<T>): SyncFlow<T, T> {
//            val iterator = source.iterator()
//            return SyncFlow(object : Producer<T>(){
//                override fun getNext(howMany: Long) {
//                    for (i in 0..howMany) {
//                        if (iterator.hasNext()) {
//                            dispatcher.onNext(iterator.next())
//                        }else{
//                            dispatcher.onComplete()
//                            break
//                        }
//                    }
//                }
//
//            }, PassthroughOperator())
//        }
//    }
//}