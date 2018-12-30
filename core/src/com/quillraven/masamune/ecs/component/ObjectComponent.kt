package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.quillraven.masamune.model.EObjectType

class ObjectComponent : Component, Pool.Poolable {
    var type = EObjectType.UNDEFINED

    override fun reset() {
        type = EObjectType.UNDEFINED
    }
}