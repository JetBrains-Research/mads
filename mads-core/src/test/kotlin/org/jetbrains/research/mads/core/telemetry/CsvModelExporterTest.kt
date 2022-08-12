package org.jetbrains.research.mads.core.telemetry

import kotlinx.coroutines.*
import java.io.File
import java.nio.ByteBuffer
import kotlin.io.path.Path
import kotlin.system.measureTimeMillis

class CsvModelExporterTest {
    private val scope = CoroutineScope(Dispatchers.IO + Job() + SupervisorJob())
    lateinit var csvModelExporter: CsvModelExporter
    lateinit var texts: MutableList<String>

    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')


    private fun prepare() {
        csvModelExporter = CsvModelExporter()
        csvModelExporter.open(
                Path(System.getProperty("user.dir") + "/" + "results" + File.separator),
            "KotlinTEST_CSV_EXPORTER2.csv",
            "Output,Output1,Output2,Output3,Output4\n"
        )
        texts = mutableListOf()

    }


    fun generateData() {
        val time = measureTimeMillis {
            for (i in 0..10_000_000) {
                texts.add(makeRandomString())
            }
        }
        println("Data generation time: $time ms")
    }


    fun writeTest() {
        println("String builder len is %s".format(texts.sumOf { s -> s.length }))
        val time = measureTimeMillis {
            scope.launch {
                csvModelExporter.flow.emit(texts)
            }
        }
        println("Data write time: $time ms")
    }

//    @Test
    fun runAll() {
        prepare()

        generateData()
        writeTest()

        closeTest()
    }

    private fun makeRandomString(): String {
        val stringBuilder = StringBuilder()
        for (j in 1..5) {
            stringBuilder.append(
                (1..8)
                    .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
                    .map(charPool::get)
                    .joinToString("")
            )
            stringBuilder.append(",")
        }
        stringBuilder.append("\n")
        return stringBuilder.toString()
    }

//    @Test
    fun doubleToString() {
        var TEST_LEN = 1_000_000
        val data = mutableListOf<Double>()
        for (i in 1..TEST_LEN) {
            data.add(kotlin.random.Random.nextDouble())
        }

        println("*** PLAIN Double To String measure time for $TEST_LEN values is %s ms"
            .format(
                measureTimeMillis {
                    var strings = data.map { d -> d.toString() }
                }
            ))

        println("*** BigDecimal Double To String measure time for $TEST_LEN values is %s ms"
            .format(
                measureTimeMillis {
                    var strings = data.map { d -> d.toBigDecimal().toPlainString() }
                }
            ))

        println("*** ByteArrays Double conversion measure time for $TEST_LEN values is %s ms"
            .format(
                measureTimeMillis {
                    var byteArray = ByteArray(data.size * 8)
                    var lastPosition = 0
                    data.forEach {
                        System.arraycopy(it.bytes(), 0, byteArray, lastPosition, 8)
                        lastPosition += 8
                    }
                }
            )
        )


    }

    private fun closeTest() {
        csvModelExporter.close()
    }

    //extension functions
    fun Double.bytes() =
        ByteBuffer.allocate(java.lang.Long.BYTES)
            .putLong(java.lang.Double.doubleToLongBits(this))
            .array()

}