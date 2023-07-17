package org.jetbrains.research.mads.core.configuration

import org.jetbrains.research.mads.core.types.ModelObject

object RootObject : ModelObject()

class TreeNode(val value: ModelObject) {
    private var parent: TreeNode? = null
    val children: MutableList<TreeNode> = mutableListOf()

    fun node(value: ModelObject, init: TreeNode.() -> Unit = {}): TreeNode {
        val childNode = TreeNode(value).apply { parent = this@TreeNode }
        childNode.init()
        children.add(childNode)
        return childNode
    }

    fun getAllObjects(): List<Pair<ModelObject, ModelObject>> {
        val pairs = mutableListOf<Pair<ModelObject, ModelObject>>()
        children.forEach { child ->
            pairs.add(Pair(value, child.value))
            pairs.addAll(child.getAllObjects())
        }
        return pairs
    }
}

class Structure() {
    private val root: TreeNode = TreeNode(RootObject)

    constructor(objects: List<ModelObject>) : this() {
        objects.forEach { root.node(it) }
    }

    fun node(value: ModelObject, init: TreeNode.() -> Unit = {}) = root.node(value, init)

    fun getAllObjects(): List<Pair<ModelObject, ModelObject>> {
        val pairsFromChildren = root.children.flatMap { it.getAllObjects() }
        val rootPair = root.children.map { Pair(root.value, it.value) }
        return rootPair + pairsFromChildren
    }

    fun isEmpty() : Boolean {
        return root.children.isEmpty()
    }
}

fun structure(init: Structure.() -> Unit): Structure {
    val configuration = Structure()
    configuration.init()
    return configuration
}