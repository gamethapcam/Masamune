package com.quillraven.masamune.ecs.component

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.quillraven.masamune.MainGame

private val bodyDef = BodyDef()
private val fixtureDef = FixtureDef()
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

fun createBody(world: World, type: BodyDef.BodyType, x: Float, y: Float, shape: Shape): Body {
    resetBodyAndFixtureDef()
    bodyDef.type = type
    bodyDef.position.set(x, y)
    bodyDef.fixedRotation = true
    return world.createBody(bodyDef).apply {
        fixtureDef.shape = shape
        fixtureDef.isSensor = false
        createFixture(fixtureDef)
        shape.dispose()
    }
}

class Box2DComponent : ISerializableComponent {
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

    override fun write(json: Json) {
        json.writeValue("type", body.type.ordinal)
        json.writeValue("x", body.position.x)
        json.writeValue("y", body.position.y)
        json.writeValue("width", width)
        json.writeValue("height", height)
    }

    override fun read(jsonData: JsonValue, game: MainGame) {
        resetBodyAndFixtureDef()
        width = jsonData.getFloat("width", 1f)
        height = jsonData.getFloat("height", 1f)

        val polygonShape = PolygonShape()
        polygonShape.setAsBox(width * 0.5f, height * 0.5f)
        body = createBody(
                game.world,
                BodyDef.BodyType.values()[jsonData.getInt("type", 0)],
                jsonData.getFloat("x", 0f),
                jsonData.getFloat("y", 0f),
                polygonShape
        )
    }


}