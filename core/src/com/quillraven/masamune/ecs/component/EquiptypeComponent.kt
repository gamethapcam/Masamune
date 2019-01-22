package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.quillraven.masamune.model.EEquipType

class EquiptypeComponent : Pool.Poolable, Component {
    var type = EEquipType.UNDEFINED

    override fun reset() {
        type = EEquipType.UNDEFINED
    }
}