package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.IntArray
import com.badlogic.gdx.utils.Pool
import com.quillraven.masamune.model.EEquipType

class EquipmentComponent : Pool.Poolable, Component {
    val equipment = IntArray(EEquipType.values().size)

    override fun reset() {
    }
}