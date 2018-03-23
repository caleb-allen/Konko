# Konko #

Konko is a Kotlin library aiming to give users the power and flexibility of [reactive streams](https://github.com/reactive-streams/reactive-streams-jvm) but with the benefit of concurrency using Kotlin coroutines. Konko was inspired by [Flow for Elixir](https://github.com/elixir-lang/flow)

Konko-Flow aims to have a similar API to the RxJava equivalent Flowable. Flow is in an early stage and the API is likely to change.
### Examples ###
```kotlin
val a = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        Flow.from(a)
                .filter { it % 2 == 0 }
                // partitions the data flow into multiple streams, on which future operations will run concurrently
                .partition()
                .map { "$it says hi!" }
                .forEach { println(it) }
```

A more complex example. This generates a map for every word in a text file, and how many time it occurs.
```
val wordCounts = Flow.from(largeTextFile)
    .partition()
    .flatMap { it.split(" ") }
    .reduceWith({ mutableMapOf<String, Int>() },
        { map, item ->
            val existing = map.getOrDefault(item, 0)
            map[item] = existing + 1
        },
        { map1, map2 ->
            map1.putAll(map2)
        }
    }

// ["the": 10, "to": 5, etc...]
```