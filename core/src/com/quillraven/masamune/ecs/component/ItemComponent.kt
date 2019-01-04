package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.quillraven.masamune.model.EItemType

class ItemComponent : Component, Pool.Poolable {
    var type: EItemType = EItemType.UNDEFINED
    var price = 0f
    // unique ID is assigned when item is moved to inventory
    var ID = -1

    override fun reset() {
        type = EItemType.UNDEFINED
        price = 0f
        ID = -1
    }
}