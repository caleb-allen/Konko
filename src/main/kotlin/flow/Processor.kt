package flow

abstract class Processor<in T, U> : Consumer<T>, Producer<U>() {

}