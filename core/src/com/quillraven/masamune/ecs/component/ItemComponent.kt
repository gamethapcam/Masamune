package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.quillraven.masamune.model.EItemType

class ItemComponent : Component, Pool.Poolable {
    var type: EItemType = EItemType.UNDEFINED
    var price = 0f
    // unique id is assigned when item is moved to inventory
    var id = -1

    override fun reset() {
        type = EItemType.UNDEFINED
        price = 0f
        id = -1
    }
}