package com.quillraven.masamune.physic

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Array

class B2DUtils constructor(private val world: World) {
    private val bodyDef = BodyDef()
    private val fixtureDef = FixtureDef()
    private val tmpBodies = Array<Body>()
    private val rectVertices = FloatArray(8)

    fun getRectVertices(width: Float, height: Float): FloatArray {
        // bot-left
        rectVertices[0] = 0f
        rectVertices[1] = 0f
        // top-left
        rectVertices[2] = 0f
        rectVertices[3] = height
        // top-right
        rectVertices[4] = width
        rectVertices[5] = height
        // bot-right
        rectVertices[6] = width
        rectVertices[7] = 0f
        return rectVertices
    }

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

    fun createBody(type: BodyDef.BodyType, x: Float, y: Float, shape: Shape, data: Any, detectionRadius: Float = 0f): Body {
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

            if (detectionRadius > 0) {
                val detBox = PolygonShape()
                detBox.setAsBox(detectionRadius * 0.5f, detectionRadius * 0.5f)
                fixtureDef.shape = detBox
                fixtureDef.isSensor = true
                createFixture(fixtureDef)
                detBox.dispose()
            }
        }
    }
}