package org.jetbrains.research.mads.core.telemetry

import kotlinx.coroutines.*
import org.jetbrains.research.mads.core.types.Response
import java.io.File
import java.nio.file.Paths
import java.util.*
import kotlin.reflect.KClass

enum class DataBroker {
    INSTANCE;
    private val scope = CoroutineScope(Dispatchers.IO + Job() + SupervisorJob())
    private val modelWriters = HashMap<KClass<out Response>, CsvModelExporter>()
//    private val metadataWriters = HashMap<MetadataEnum, CsvModelExporter>()


    fun mkdirs(dir: String) {
        File(dir).mkdirs()
    }

    fun initModelWriters(dir: String, allowedResponses: Set<KClass<out Response>>) {
        mkdirs(dir)
        allowedResponses.forEach {
            val csvModelExporter = CsvModelExporter()
            csvModelExporter.open(
                Paths.get(dir, File.separator),
                "${it.simpleName}s.csv",
                ""
            )
            modelWriters[it] = csvModelExporter
        }
    }

//    fun initMetadataWriters(dir: String, uuid: UUID) {
//        mkdirs(dir)
//        val csvMetadataExporter = CsvModelExporter()
//        csvMetadataExporter.open(
//            Paths.get(dir, File.separator),
//            uuid.toString() + MetadataStrings.INSTANCE.getCsvFileName(MetadataEnum.SpaceSnapshot),
//            MetadataStrings.INSTANCE.getCsvHeader(MetadataEnum.SpaceSnapshot)
//        )
//
//        metadataWriters[MetadataEnum.SpaceSnapshot] = csvMetadataExporter
//    }

//    @OptIn(InternalCoroutinesApi::class)
//    fun logResponses(tick: Long, responses: Array<Response>) {
//        scope.launch {
//            responses
//                .groupBy { it::class }
//                .map {
//                    it.key to
//                            it.value.map { response ->
//                                response.getCsvString(tick)
//                            }
//                }
//                .forEach {
//                    scope.launch {
//                        modelWriters[it.first]!!.flow.emit(it.second)
//                    }
//                }
//        }
//    }

    fun logResponse(tick: Long, response: Response): Response {
        if (modelWriters.containsKey(response::class) && response.logResponse) {
            scope.launch {
                modelWriters[response::class]!!.flow.emit(response.response)
            }
        }
        return response
    }

    fun closeModelWriters() {
        Thread.sleep(10000) //give them time to write it all from flows
        modelWriters.values.forEach {
            it.close()
        }
    }

//    fun closeMetadataWriters() {
//        metadataWriters.values.forEach {
//            it.close()
//        }
//    }

    fun isClosed(): Boolean {
        return (modelWriters.values.map { it.isClosed }.toList() //+
//                metadataWriters.values.map { it.isClosed }.toList()
                )
            .reduceOrNull { acc, next -> acc && next }
            ?: true

    }
}