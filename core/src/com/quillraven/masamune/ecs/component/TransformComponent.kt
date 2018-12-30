package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class TransformComponent : Component, Pool.Poolable {
    var width = 0f
    var height = 0f

    var x = 0f
    var y = 0f
    @Transient
    var prevX = 0f
    @Transient
    var prevY = 0f
    @Transient
    var interpolatedX = 0f
    @Transient
    var interpolatedY = 0f

    var angle = 0f
    @Transient
    var prevAngle = 0f
    @Transient
    var interpolatedAngle = 0f

    override fun reset() {
        width = 0f
        height = 0f

        x = 0f
        y = 0f
        prevX = 0f
        prevY = 0f
        interpolatedX = 0f
        interpolatedY = 0f

        angle = 0f
        prevAngle = 0f
        interpolatedAngle = 0f
    }
}