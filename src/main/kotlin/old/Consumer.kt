package old

interface Consumer<in T>{
    fun onNext(item: T)
    fun onComplete()
}