import channel.*
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

    @Test fun fileTest() {
        val time = measureTimeMillis {
            val f = File("G:\\Downloads\\big.txt")
            val count = Flow.from(f)
//                    .limit(1000)
//                    .flatMap { it.split(" ", "\n") }
//                    .limit(3)
                    .flatMap { it.toList() }
                    .count()
//                    .map { it + " Hi" }
//                    .limit(10)
//                    .forEach { println(it) }
//                    .consumeEach { println(it) }

            println("Character count: $count")
        }

        println("Time: ${time}ms")
    }


    @Test fun syncTest(){
        val time = measureTimeMillis {
            val letterCountMap = mutableMapOf<Char, Int>()
            var count = 0

            val f = File("G:\\Downloads\\big.txt")
            val br = BufferedReader(FileReader(f))
            var s : String? = br.readLine()
            var linesRead = 0

            while (s != null /*&& linesRead < 10*/) {
                val chars = s.toCharArray()
                linesRead++
                count += chars.toList().size
                chars.forEach {
                    letterCountMap[it] = letterCountMap.getOrPut(it, {0}) + 1
                }
                s = br.readLine()
            }
            br.close()

            println(letterCountMap)
            letterCountMap.entries.toList().subList(0, 10).forEach {
                println(it)
            }

            println("Character count: $count")
        }

        println("Time: ${time}ms")
    }
}