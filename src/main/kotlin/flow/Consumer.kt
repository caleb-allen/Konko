package flow

interface Consumer<in T>{
    fun onNext(item: T)
    fun onComplete()
}