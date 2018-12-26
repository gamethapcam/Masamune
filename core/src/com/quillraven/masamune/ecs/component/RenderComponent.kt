package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.StringBuilder

class RenderComponent : Pool.Poolable, Component {
    var sprite: Sprite? = null
    val texturePath = StringBuilder()
    var flipX = false
    var flipY = false
    var width = 1f
    var height = 1f

    override fun reset() {
        sprite = null
        texturePath.setLength(0)
        flipX = false
        flipY = false
        width = 1f
        height = 1f
    }
}