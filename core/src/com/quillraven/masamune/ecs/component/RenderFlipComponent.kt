package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class RenderFlipComponent : Pool.Poolable, Component {
    var flipCountdown = 0f

    override fun reset() {
        flipCountdown = 0f
    }
}