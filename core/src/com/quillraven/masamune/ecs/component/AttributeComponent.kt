package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.FloatArray
import com.badlogic.gdx.utils.Pool
import com.quillraven.masamune.model.EAttributeType

class AttributeComponent : Component, Pool.Poolable {
    val attributes = FloatArray(EAttributeType.values().size)

    override fun reset() {
        for (i in attributes.items.indices) {
            attributes[i] = 0f
        }
    }
}