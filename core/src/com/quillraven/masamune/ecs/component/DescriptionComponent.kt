package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class DescriptionComponent : Pool.Poolable, Component {
    var name = ""
    var description = ""

    override fun reset() {
        name = ""
        description = ""
    }
}