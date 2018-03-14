//package example
//
//import flow.Dispatcher
//import kotlinx.coroutines.experimental.launch
//import kotlinx.coroutines.experimental.runBlocking
//import java.util.*
//
//class ConcurrentDispatcher<T>(private val min: Long = 4) : Dispatcher<T>() {
//    private val itemQueue = LinkedList<T>()
//    override fun onNext(item: T) {
//        itemQueue.add(item)
//
//        if (itemQueue.size >= min) {
//            flushItems()
//        }
//    }
//
//    override fun onComplete() {
//        flushItems()
//        super.onComplete()
//    }
//
//    private fun flushItems(){
//        var consumerIndex = 0
//        val consumerSize = consumers.size
//        val items = itemQueue.clone()
//
//        runBlocking {
//            items.forEach {item ->
//                val i = consumerIndex
//                launch {
//                    consumers[i].onNext(item)
//                }
//                consumerIndex++
//                if (consumerIndex == consumerSize) {
//                    consumerIndex = 0
//                }
//            }
//
//        }
//    }
//}