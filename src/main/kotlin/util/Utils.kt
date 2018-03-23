package util

/**
 * Run with `-Dkotlinx.coroutines.debug` JVM option to see coroutines
 */
fun <T> log(msg: T) = println("[${Thread.currentThread().name}] $msg")
//fun <T> log(msg: T) {}
