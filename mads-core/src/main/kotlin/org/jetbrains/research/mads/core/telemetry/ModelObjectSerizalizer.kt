package org.jetbrains.research.mads.core.telemetry

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.jetbrains.research.mads.core.types.ConnectionType
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Signals
import java.util.Dictionary
import kotlin.reflect.KClass
import kotlin.reflect.jvm.internal.impl.resolve.constants.KClassValue
import kotlin.reflect.cast

import kotlinx.serialization.encodeToString

@OptIn(InternalSerializationApi::class)
class ModelObjectSerizalizer: KSerializer<ModelObject> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ModelObject") {
        element<String>("type")
        element<String>("id")
        element<String>("parentId")
        element<List<String>>("connections")
        element<Map<String, Map<String, Double>>>("signals")
    }

    override fun deserialize(decoder: Decoder): ModelObject {
        TODO("Not yet implemented")
    }

    override fun serialize(encoder: Encoder, value: ModelObject) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor,0, value.type)
            encodeStringElement(descriptor,1, value.hashCode().toString())
            encodeStringElement(descriptor,2, value.parent.hashCode().toString())
            encodeSerializableElement(descriptor
                ,3
                , ListSerializer(String.serializer()) //TODO: change to map
                , value.connections.flatMap {
                    connOfType -> connOfType.value.map {
                        connOfType.key::class.simpleName+"→"+it.hashCode().toString()
                    }
                }
            )
            encodeSerializableElement(descriptor
                ,4
                , MapSerializer(String.serializer(), MapSerializer(String.serializer(), Double.serializer()))
                , value.signals.map {
                    (k,v) -> (k.simpleName ?: "No Simple Name") to v.state()
//                    (k,v) -> (k.simpleName ?: "No Simple Name") to v.toString()
//                    (k,v) -> (k.simpleName ?: "No Simple Name") to Json{encodeDefaults=true}.encodeToString(v)
//                    (k,v) -> (k.simpleName ?: "No Simple Name") to Json.encodeToString(k.serializer(), v)
                }.toMap()
            )

        }
    }
}

