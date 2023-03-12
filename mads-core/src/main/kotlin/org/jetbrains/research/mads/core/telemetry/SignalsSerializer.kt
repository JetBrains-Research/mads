package org.jetbrains.research.mads.core.telemetry

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.*
import org.jetbrains.research.mads.core.types.Signals

class SignalsSerializer: KSerializer<Signals> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Signals") {
        element<Map<String, JsonElement>>("signals")
    }

    override fun deserialize(decoder: Decoder): Signals {
        TODO("Not yet implemented")
    }

    override fun serialize(encoder: Encoder, value: Signals) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor
                ,0
                , MapSerializer(String.serializer(), JsonElement.serializer())
                , value.getProperties().map {
                    (k, v) -> k to v.toJsonElement()
                }.toMap()
            )
        }
    }

    fun Any?.toJsonElement(): JsonElement =
        when (this) {
            null -> JsonNull
            is Map<*, *> -> toJsonElement()
            is Collection<*> -> toJsonElement()
            is Boolean -> JsonPrimitive(this)
            is Number -> JsonPrimitive(this)
            is String -> JsonPrimitive(this)
            is Enum<*> -> JsonPrimitive(this.toString())
            else -> throw IllegalStateException("Can't serialize unknown type: $this")
        }

    private fun Collection<*>.toJsonElement(): JsonElement {
        val list: MutableList<JsonElement> = mutableListOf()
        this.forEach { value ->
            when (value) {
                null -> list.add(JsonNull)
                is Map<*, *> -> list.add(value.toJsonElement())
                is Collection<*> -> list.add(value.toJsonElement())
                is Boolean -> list.add(JsonPrimitive(value))
                is Number -> list.add(JsonPrimitive(value))
                is String -> list.add(JsonPrimitive(value))
                is Enum<*> -> list.add(JsonPrimitive(value.toString()))
                else -> throw IllegalStateException("Can't serialize unknown collection type: $value")
            }
        }
        return JsonArray(list)
    }

    private fun Map<*, *>.toJsonElement(): JsonElement {
        val map: MutableMap<String, JsonElement> = mutableMapOf()
        this.forEach { (key, value) ->
            key as String
            when (value) {
                null -> map[key] = JsonNull
                is Map<*, *> -> map[key] = value.toJsonElement()
                is Collection<*> -> map[key] = value.toJsonElement()
                is Boolean -> map[key] = JsonPrimitive(value)
                is Number -> map[key] = JsonPrimitive(value)
                is String -> map[key] = JsonPrimitive(value)
                is Enum<*> -> map[key] = JsonPrimitive(value.toString())
                else -> throw IllegalStateException("Can't serialize unknown type: $value")
            }
        }
        return JsonObject(map)
    }
}