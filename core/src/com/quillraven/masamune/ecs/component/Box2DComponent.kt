package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Pool

class Box2DComponent : Pool.Poolable, Component {
    lateinit var body: Body
    var width = 0f
    var height = 0f
    var prevX = 0f
    var prevY = 0f
    var prevAngle = 0f
    var interpolatedX = 0f
    var interpolatedY = 0f
    var interpolatedAngle = 0f

    override fun reset() {
        body.world.destroyBody(body)
        width = 0f
        height = 0f
        prevX = 0f
        prevY = 0f
        prevAngle = 0f
        interpolatedX = 0f
        interpolatedY = 0f
        interpolatedAngle = 0f
    }
}