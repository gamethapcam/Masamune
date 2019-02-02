package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class ConsumableComponent : Pool.Poolable, Component {
    override fun reset() {
    }
}