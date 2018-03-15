package old

abstract class Processor<in T, U> : Consumer<T>, Producer<U>() {

}