package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Pool

class Box2DComponent : Component, Pool.Poolable {
    @Transient
    lateinit var body: Body
    var type = 0
    var detectionRadius = 0f

    override fun reset() {
        body.userData = null
        body.world.destroyBody(body)
        type = 0
        detectionRadius = 0f
    }
}