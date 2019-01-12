package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.quillraven.masamune.ecs.EntityType
import com.quillraven.masamune.ecs.system.DEFAULT_ENTITY_ID
import com.quillraven.masamune.model.ObjectType

class IdentifyComponent : Component, Pool.Poolable {
    var id = DEFAULT_ENTITY_ID
    var entityType = EntityType.UNDEFINED
    var type = ObjectType.UNDEFINED

    override fun reset() {
        id = DEFAULT_ENTITY_ID
        entityType = EntityType.UNDEFINED
        type = ObjectType.UNDEFINED
    }
}