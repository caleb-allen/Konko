package flow

interface Operator<in T, U> {
    val batchSize: Int
    fun apply(items: Collection<T>): U
}

class BaseOperator<in T, U>(val transform: (T) -> U,
                            override val batchSize: Int = 1): Operator<T, U>{
    // can't be a 1 to 1 apply because we don't know what the transform wants to do with it
    override fun apply(items: Collection<T>): U{
        if (items.size != batchSize) {
            throw IllegalStateException("Sent items larger than batch size\nBatch Size: $batchSize\n" +
                    "Collection Size: ${items.size}")
        }
        val item = items.first()
        return transform(item)
    }


}