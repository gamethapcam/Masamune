package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class DescriptionComponent : Pool.Poolable, Component {
    // name and description are applied in DescriptionSystem, therefore persistence is not needed
    @Transient
    var name = ""
    @Transient
    var description = ""

    override fun reset() {
        name = ""
        description = ""
    }
}