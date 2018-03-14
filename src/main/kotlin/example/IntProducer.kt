package example

import flow.Processor
import flow.Producer
import java.util.*

class IntProducer : Producer<Int>() {
    override fun getNext(howMany: Long) {
        val r = Random()
        for (i in 0..howMany) {
//            dispatcher.onNext(r.nextInt(100))
            dispatcher.onNext(i.toInt())
        }
        dispatcher.onComplete()
    }
}

class AddOneProcessor : Processor<Int, Int>() {
    override fun onNext(item: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onComplete() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNext(howMany: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}