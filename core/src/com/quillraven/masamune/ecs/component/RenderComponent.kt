package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class RenderComponent : Pool.Poolable, Component {
    var flipX = false
    var flipY = false

    override fun reset() {
        flipX = false
        flipY = false
    }
}