package example

import old.Dispatcher

/**
 * Simple dispatcher which emits every item to every consumer
 */
class SimpleDispatcher<T> : Dispatcher<T>() {
    override fun onNext(item: T) {
        consumers.forEach {
            it.onNext(item)
        }
    }
}