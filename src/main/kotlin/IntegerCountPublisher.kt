import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber

class IntegerCountPublisher(val start: Int, val end: Int) : Publisher<Int> {
    override fun subscribe(s: Subscriber<in Int>) {
//        val subscription = IntCountSubscription(start, end, s)
//        s.onSubscribe(subscription)
    }
}