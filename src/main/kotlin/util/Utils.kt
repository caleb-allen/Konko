package util

/**
 * Run with `-Dkotlinx.coroutines.debug` JVM option to see coroutines
 */
fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")
