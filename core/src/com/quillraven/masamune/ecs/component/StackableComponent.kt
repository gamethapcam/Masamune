package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class StackableComponent : Pool.Poolable, Component {
    var size = 1

    override fun reset() {
        size = 1
    }
}