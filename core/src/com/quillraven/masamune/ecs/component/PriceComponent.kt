package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class PriceComponent : Pool.Poolable, Component {
    var price = 0

    override fun reset() {
        price = 0
    }
}