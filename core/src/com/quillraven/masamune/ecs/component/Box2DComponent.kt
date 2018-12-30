package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Pool

class Box2DComponent : Component, Pool.Poolable {
    @Transient
    lateinit var body: Body
    var type = 0

    override fun reset() {
        body.world.destroyBody(body)
        type = 0
    }
}