package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.IntArray
import com.badlogic.gdx.utils.Pool

class InventoryComponent : Component, Pool.Poolable {
    val items = IntArray(0)
    var maxSize = 0

    override fun reset() {
        items.clear()
        maxSize = 0
    }
}