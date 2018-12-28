package com.quillraven.masamune.ecs.component

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.quillraven.masamune.MainGame

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
        width = jsonData.getFloat("width", 1f)
        height = jsonData.getFloat("height", 1f)

        val polygonShape = PolygonShape()
        polygonShape.setAsBox(width * 0.5f, height * 0.5f)
        body = game.b2dUtils.createBody(
                BodyDef.BodyType.values()[jsonData.getInt("type", 0)],
                jsonData.getFloat("x", 0f),
                jsonData.getFloat("y", 0f),
                polygonShape
        )
    }
}