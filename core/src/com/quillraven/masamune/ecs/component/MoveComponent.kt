package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class MoveComponent : Pool.Poolable, Component {
    var speed = 0f

    override fun reset() {
        speed = 0f
    }
}