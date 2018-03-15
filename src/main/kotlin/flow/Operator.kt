package flow

import kotlinx.coroutines.experimental.channels.ReceiveChannel

interface Operator {
    val isStateful: Boolean
}

/**
 * applies lambda transform expression to received stream.
 */
abstract class IntermediateOperator<in T, out U>(private val transform: (T) -> U): Operator{
    suspend fun apply(items: ReceiveChannel<T>): U {
        return transform(items.receive())
    }
}

open class TerminalOperator<in T> : Operator {
    override val isStateful: Boolean = true
}

open class StatelessOperator<in T, out U>(transform: (T) -> U): IntermediateOperator<T, U>(transform){
    override val isStateful: Boolean = false
}

open class StatefulOperator<in T, out U>(transform: (T) -> U): IntermediateOperator<T, U>(transform){
    override val isStateful: Boolean = true
}


class PassthroughOperator<T> : StatelessOperator<T, T>({ it })