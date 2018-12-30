package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.Pool

class RenderComponent : Component, Pool.Poolable {
    @Transient
    lateinit var sprite: Sprite
    var texture = ""
    var flipX = false
    var flipY = false
    var width = 1f
    var height = 1f

    override fun reset() {
        texture = ""
        flipX = false
        flipY = false
        width = 1f
        height = 1f
    }
}