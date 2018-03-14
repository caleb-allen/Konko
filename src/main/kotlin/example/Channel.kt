package example

import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.ChannelIterator
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.selects.SelectClause1
import kotlinx.coroutines.experimental.selects.SelectClause2

class FlowChannel<T>: Channel<T> {
    override val isClosedForReceive: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val isClosedForSend: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val isEmpty: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val isFull: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val onReceive: SelectClause1<T>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val onReceiveOrNull: SelectClause1<T?>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val onSend: SelectClause2<T, SendChannel<T>>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun cancel(cause: Throwable?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun close(cause: Throwable?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun iterator(): ChannelIterator<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun offer(element: T): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun poll(): T? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun receive(): T {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun receiveOrNull(): T? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun send(element: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}