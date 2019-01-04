package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class AttributeComponent : Component, Pool.Poolable {
    var strength = 0
    var intelligence = 0

    override fun reset() {
        strength = 0
        intelligence = 0
    }
}