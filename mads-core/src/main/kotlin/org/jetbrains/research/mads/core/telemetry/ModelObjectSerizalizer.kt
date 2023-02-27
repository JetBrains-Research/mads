package org.jetbrains.research.mads.core.telemetry

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
import org.jetbrains.research.mads.core.types.ConnectionType
import org.jetbrains.research.mads.core.types.ModelObject
import java.util.Dictionary

class ModelObjectSerizalizer: KSerializer<ModelObject> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ModelObject") {
        element<String>("type")
        element<String>("id")
        element<String>("parentId")
        element<List<String>>("connections")
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
                , ListSerializer(String.serializer())
                , value.connections.flatMap { connOfType -> connOfType.value.map { connOfType.key::class.simpleName+'→'+it.hashCode().toString() } }
            )
        }
    }
}