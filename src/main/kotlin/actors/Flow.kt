package actors

import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor

class Flow<T, U> private constructor(upstream: ReceiveChannel<T>): SendChannel<U> by someDelegate(){



    companion object {
        // maybe we use the downstream to send messages back to a "mediator" that will pass it to the next stage
        fun <T> filterActor(downstream: SendChannel<T>, predicate: (T) -> Boolean): SendChannel<T>{
            val a = actor<T> {
                for (item in channel) {
                    // need to pass this downstream reference down the downstream reference down the chain
                    if (predicate(item)) {
                        downstream.send(item)
                    }
                }
            }
            return a
        }


        fun <T> forEach(block: (T) -> Unit){
            actor<T> {
                for (item in channel) {
                    block(item)
                }
            }
        }
    }

}

fun <T> someDelegate(): SendChannel<T> {
    TODO()
}

//class MapActor<T>: SendChannel<T>

