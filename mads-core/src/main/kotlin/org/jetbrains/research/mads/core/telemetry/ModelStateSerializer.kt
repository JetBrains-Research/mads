package org.jetbrains.research.mads.core.telemetry

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.types.ModelObject

object ModelStateSerializer: KSerializer<Model> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Model") {
        element<Long>("time")
        element<List<ModelObject>>("objects")
    }

    override fun deserialize(decoder: Decoder): Model {
        TODO("Not yet implemented")
    }

    override fun serialize(encoder: Encoder, value: Model) {
        encoder.encodeStructure(descriptor) {
            encodeLongElement(descriptor, 0, value.tStart)
            encodeSerializableElement(descriptor,1, ListSerializer(ModelObject.serializer()),value.recursivelyGetChildObjects())
        }
    }

}