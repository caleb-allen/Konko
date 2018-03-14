import example.IntProducer
import flow.Consumer
import org.junit.Test

class IntTest{
    @Test fun testMyFlowStuff(){
        val producer = IntProducer()
        val consumer1 = IntConsumer("1")
        val consumer2 = IntConsumer("2")
        producer.subscribe(consumer1)
        producer.subscribe(consumer2)
        producer.getNext(20)
    }
}

class IntConsumer(val name: String) : Consumer<Int> {
    override fun onNext(item: Int) {
//        println("Consumer $name: $item")
    }

    override fun onComplete() {
        println("Complete")
    }

}