package org.jetbrains.research.mads.core.types

import kotlinx.serialization.Serializable
import org.jetbrains.research.mads.core.telemetry.SignalsSerializer
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType

abstract class ObservableProperty<T>(initialValue: T) : ReadWriteProperty<Any?, T> {
    private var value: T = initialValue

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
        onValueChanged(property)
    }

    protected abstract fun onValueChanged(property: KProperty<*>)
}

@Serializable(with = SignalsSerializer::class)
open class Signals {
    private val updatedProperties = mutableSetOf<KProperty<*>>()

    fun <T> observable(initialValue: T): ObservableProperty<T> {
        return object : ObservableProperty<T>(initialValue) {
            override fun onValueChanged(property: KProperty<*>) {
                @Suppress("UNCHECKED_CAST")
                updatedProperties.add(property as KProperty1<Signals, *>)
            }
        }
    }

    fun getUpdatedProperties(): Map<String, String> {
        val updatedMap = mutableMapOf<String, String>()
        for (prop in updatedProperties) {
            @Suppress("UNCHECKED_CAST")
            val kProp = prop as? KProperty1<Signals, *>
            kProp?.let {
                val value = kProp.get(this)?.toString() ?: "null"
                updatedMap[kProp.name] = value
            }
        }
        updatedProperties.clear()
        return updatedMap
    }

    fun getProperties(): Map<String, Any> {
        val kClass = this::class
        val memberProperties = kClass.memberProperties

        val updatedMap = mutableMapOf<String, Any>()
        memberProperties.forEach {
            @Suppress("UNCHECKED_CAST")
            val kProp = it as? KProperty1<Signals, *>
            kProp?.let {
                updatedMap[kProp.name] = kProp.get(this) as Any
            }
        }
        return updatedMap
    }

    override fun toString(): String {
        val kClass = this::class
        val memberProperties = kClass.memberProperties

        var resultStr = "\"${kClass.simpleName}\":{"
        memberProperties.forEach {
            @Suppress("UNCHECKED_CAST")
            val kProp = it as? KProperty1<Signals, *>
            val isNumberOrBoolean: Boolean = kProp?.returnType == Double::class.starProjectedType
                    || kProp?.returnType == Float::class.starProjectedType
                    || kProp?.returnType == Int::class.starProjectedType
                    || kProp?.returnType == Long::class.starProjectedType
                    || kProp?.returnType == Boolean::class.starProjectedType

            resultStr += if(isNumberOrBoolean) {
                "\"${kProp?.name}\":${kProp?.get(this)},"
            } else {
                "\"${kProp?.name}\":\"${kProp?.get(this)}\","
            }
        }

        return resultStr.dropLast(1) + "}"
    }
}