import flow.Flow
import org.junit.Test
import java.util.*

class FlowTest {
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
//        val a = listOf(0, 1, 2, 3)

        val numberProducerFlow = Flow.fromIterable(a)

//        val consumer1 = IntConsumer("1")
//        val consumer2 = IntConsumer("2")
        val startTime = System.currentTimeMillis()
        println("Start time: $startTime")
        numberProducerFlow
                .map { Math.sqrt(it.toDouble()) }
                .subscribe (onCompleteCallback = {
                    println("Completed")
                    val endTime = System.currentTimeMillis()
                    println("End time: $endTime")
                    val length = endTime - startTime
                    println("Time taken: ${length.toDouble() / 1000}s")
                }, onNextCallback = {
//                    println(it)
                })
                .getNext(20_000_000)
    }
}