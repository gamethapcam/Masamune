package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class HealComponent : Component, Pool.Poolable {
    var life = 0f
    var mana = 0f

    override fun reset() {
        life = 0f
        mana = 0f
    }
}