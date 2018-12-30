package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class RenderFlipComponent : Component, Pool.Poolable {
    var counter = 0f

    override fun reset() {
        counter = 0f
    }
}