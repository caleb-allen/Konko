import channel.*
import io.reactivex.Flowable
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import kotlin.system.measureTimeMillis

class ChannelTest {
    @Test fun testFlowWithChannels() {
        val upstream = produce(capacity = Channel.UNLIMITED) {
            for (i in 0..1_000_000) {
                send(i)
            }
        }

        val time = measureTimeMillis {
            Flow.from(upstream)
                    .filter {/* println("#1 filtering $it");*/it % 2 == 0 }
                    .filter { /*println("#2 filtering $it"); */it % 3 == 0 }
                    .map { /*println("mapping $it");*/"$it says hello" }
                    .forEach {  }
        }

        println("Total time: $time ms")
    }

    @Test fun collectionTest(){
        val a = listOf(0, 1, 2, 3, 4, 5)
        Flow.from(a)
                .flatMap { listOf(it, it * it) }
                .forEach { println(it) }
    }

    @Test fun multipleChannelsTest(){
        val a = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        Flow.from(a)
                .filter { it % 2 == 0 }
                .map { "$it says hi!" }
                .forEach { println(it) }
    }

    @Test fun terminalOpTest(){
        val a = listOf(0, 1, 2, 3, 4)
        Flow.from(a)
                .reduce {item1, item2 -> item1 + item2}
    }

    @Test fun fileTest() {
        runBlocking {
            val time = measureTimeMillis {
                val f = File("G:\\Downloads\\big.txt")
                Flow.from(f)
                        .flatMap { it.split(" ") }
                        .consumeEach {  }
            }
            println("Time: ${time}ms")
        }
    }

    @Test fun collectTest() {
        val time = measureTimeMillis {
            val f = File("G:\\Downloads\\big.txt")
            val wordsCount = Flow.from(f)
                    .flatMap { it.split(" ") }
                    .collect(
                            { mutableMapOf<String, Int>() },
                            { map, item ->
                                val existing = map.getOrDefault(item, 0)
                                map[item] = existing + 1
                            },
                            { map1, map2 ->
                                map1.putAll(map2)
                            }
                    )

            println("Got word counts!")
            println("Total words: ${wordsCount.size}")

            println("Grabbing random 10")
            val entriesList = wordsCount.entries.toList().subList(0, 10)
            for (entry in entriesList) {
                println(entry)
            }
        }
//        println("Time: ${time}ms")
    }

    @Test fun reactiveTest(){
        val time = measureTimeMillis {
            val f = File("G:\\Downloads\\big.txt")
            val br = BufferedReader(FileReader(f))
            val count = Flowable.generate<String> {

                val s : String? = br.readLine()
                if (s != null /*&& linesRead < 10*/) {
                    it.onNext(s)
                }else{
                    it.onComplete()
                }
            }
                    .flatMap { Flowable.fromIterable(it.split(" ")) }
                    .filter { it == "the" }
                    .count()
            println("Count: ${count.blockingGet()}")

        }
        println("Time: ${time}ms")

    }


    @Test fun syncTest(){
        val time = measureTimeMillis {
//            val letterCountMap = mutableMapOf<Char, Int>()
            var count = 0

            val f = File("G:\\Downloads\\big.txt")
            val br = BufferedReader(FileReader(f))
            var s : String? = br.readLine()
            var linesRead = 0

            while (s != null /*&& linesRead < 10*/) {
                val words = s.split(" ")
                linesRead++
                count += words.filter { it == "the" }.count()
                s = br.readLine()
            }
            br.close()
//
//            println(letterCountMap)
//            letterCountMap.entries.toList().subList(0, 10).forEach {
//                println(it)
//            }

            println("Words matching count: $count")
        }

        println("Time: ${time}ms")
    }
}