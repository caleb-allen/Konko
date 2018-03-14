import flow.Flow
import org.junit.Test

class FlowTest {
    @Test
    fun testFlow(){
        val a = listOf(1, 4, 2, 6, 2, 7, 45, 23)

        val numberProducerFlow = Flow.fromIterable(a)

        val consumer1 = IntConsumer("1")
//        val consumer2 = IntConsumer("2")

        numberProducerFlow.getNext(10)
    }
}