import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.channels.asReceiveChannel
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test
import java.util.*
import kotlin.system.measureTimeMillis

class SyncFlowTest {
    @Test
    fun testFlow(){
        val a = mutableListOf<Int>()

        val r = Random()
        for (i in 0..10_000_000) {
            a.add(r.nextInt(500_000))
        }

//        for (i in 0..10) {
//            a.add(r.nextInt(500_000))
//        }
////        val a = listOf(0, 1, 2, 3)
//
//        val numberProducerFlow = SyncFlow.fromIterable(a)
//
////        val consumer1 = IntConsumer("1")
////        val consumer2 = IntConsumer("2")
//        val startTime = System.currentTimeMillis()
//        println("Start time: $startTime")
//        numberProducerFlow
//                .map { Math.sqrt(it.toDouble()) }
//                .subscribe (onCompleteCallback = {
//                    println("Completed")
//                    val endTime = System.currentTimeMillis()
//                    println("End time: $endTime")
//                    val length = endTime - startTime
//                    println("Time taken: ${length.toDouble() / 1000}s")
//                }, onNextCallback = {
////                    println(it)
//                })
//                .getNext(20_000_000)
    }

    @Test fun testActor() = runBlocking {
        val addFive = actor<Int>{
            for (msg in channel) {

            }
        }

        val printNums = actor<Int>{
            for (msg in channel) {
                println(msg)
            }
        }
        val time = measureTimeMillis {
            val items = listOf(0, 1, 2, 3, 4, 5)
            val jobs = List(items.size) {
                launch { printNums.send(it) }
            }
            jobs.forEach { it.join() }
        }

        printNums.close()

        println("Took $time ms")
    }

    @Test fun testChannels(){
        val items = listOf(0, 1, 2, 3, 4, 5)
        items.asReceiveChannel()
    }
}