package com.quillraven.masamune.ecs.component

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.quillraven.masamune.MainGame

class Box2DComponent : ISerializableComponent {
    lateinit var body: Body
    var bodyType = 0

    override fun reset() {
        body.world.destroyBody(body)
        bodyType = 0
    }

    override fun write(json: Json) {
        json.writeValue("type", body.type.ordinal)
    }

    override fun read(jsonData: JsonValue, game: MainGame) {
        bodyType = jsonData.getInt("type")
    }
}