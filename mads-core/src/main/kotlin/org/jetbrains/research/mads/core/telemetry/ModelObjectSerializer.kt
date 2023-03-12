package org.jetbrains.research.mads.core.telemetry

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.*
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Signals

@OptIn(InternalSerializationApi::class)
class ModelObjectSerializer: KSerializer<ModelObject> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ModelObject") {
        element<String>("type")
        element<String>("id")
        element<String>("parentId")
        element<List<String>>("connections")
        element<Map<String, Signals>>("signals")
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
                , MapSerializer(String.serializer(), Signals.serializer())
                , value.signals.map {
                    (k, v) -> (k.simpleName ?: "No Simple Name") to v
                }.toMap()
            )
        }
    }
}

