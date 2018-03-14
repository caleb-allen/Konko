import org.reactivestreams.Publisher
import org.reactivestreams.tck.PublisherVerification
import org.reactivestreams.tck.TestEnvironment

class KPublisherVerification : PublisherVerification<Int>(TestEnvironment()) {
    override fun createPublisher(elements: Long): Publisher<Int> {
        return IntegerCountPublisher(0, 5_000_000)
    }

    override fun createFailedPublisher(): Publisher<Int>? {
        return null
    }
}