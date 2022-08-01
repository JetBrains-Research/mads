package org.jetbrains.research.mads.core.configuration

import org.jetbrains.research.mads.core.types.ModelObject
import kotlin.reflect.KClass

class Configuration {
    /* 1. Эксперимент определяется конфигурацией
       2. Набор типов объектов, с которыми мы работаем в эксперименте, мы прописываем в конфигурации
       3. Механизмы и их параметры над конкретными типами объектов мы прописываем в конфигурации (соотношение 1 тип объекта -> много механизмов)
       4. Условия для выполнения механизмов мы прописываем в конфигурации (1 механизм -> 1 условие)

       В пункте 2 тип объекта это стандартный тип
       В пункте 3 и 4 это функциональные типы

       ВАЖНО: в пункте 3 можно вместо механизма в конфигурации сразу хранить события, но тогда при создании нового объекта
       с типом таким-то (например, клетка)необходимо весь набор событий копировать.
       Используем шаблон с заранее созданными событиями
    */

    private val objPathways: HashMap<KClass<out ModelObject>, ArrayList<Pathway<out ModelObject>>> = HashMap()

    fun add(objectType: KClass<out ModelObject>, pathways: ArrayList<Pathway<out ModelObject>>) {
        objPathways[objectType] = pathways
    }

    fun createEvents(obj: ModelObject) {
        objPathways[obj::class]!!.forEach {
            obj.createEvents(it)
        }
    }
}