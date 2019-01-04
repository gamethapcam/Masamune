package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool

class InventoryComponent : Component, Pool.Poolable {
    @Transient
    val items = Array<Entity>()
    var maxSize = 0

    override fun reset() {
        items.clear()
        maxSize = 0
    }
}