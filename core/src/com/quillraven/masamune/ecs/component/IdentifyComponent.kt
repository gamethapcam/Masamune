package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.quillraven.masamune.ecs.EntityType
import com.quillraven.masamune.model.ObjectType

class IdentifyComponent : Component, Pool.Poolable {
    var id = -1
    var entityType = EntityType.UNDEFINED
    var type = ObjectType.UNDEFINED

    override fun reset() {
        id = -1
        entityType = EntityType.UNDEFINED
        type = ObjectType.UNDEFINED
    }
}