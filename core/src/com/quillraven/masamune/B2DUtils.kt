package com.quillraven.masamune

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Array

class B2DUtils constructor(private val world: World) {
    private val bodyDef = BodyDef()
    private val fixtureDef = FixtureDef()
    private val tmpBodies = Array<Body>()

    private fun resetBodyAndFixtureDef() {
        bodyDef.apply {
            type = BodyDef.BodyType.StaticBody
            position.set(Vector2.Zero)
            angle = 0f
            linearVelocity.set(Vector2.Zero)
            angularVelocity = 0f
            linearDamping = 0f
            angularDamping = 0f
            allowSleep = true
            awake = true
            fixedRotation = false
            bullet = false
            active = true
            gravityScale = 1f
        }

        fixtureDef.apply {
            shape = null
            friction = 0.2f
            restitution = 0f
            density = 0f
            isSensor = false
            filter.categoryBits = 0x0001
            filter.maskBits = -1
            filter.groupIndex = 0
        }
    }

    fun destroyBodies(filter: Any) {
        world.getBodies(tmpBodies)
        for (body in tmpBodies) {
            if (body.userData == filter) {
                world.destroyBody(body)
            }
        }
    }

    fun createBody(type: BodyDef.BodyType, x: Float, y: Float, shape: Shape, data: Any): Body {
        resetBodyAndFixtureDef()
        bodyDef.type = type
        bodyDef.position.set(x, y)
        bodyDef.fixedRotation = true
        return world.createBody(bodyDef).apply {
            fixtureDef.shape = shape
            fixtureDef.isSensor = false
            createFixture(fixtureDef)
            userData = data
            shape.dispose()
        }
    }
}