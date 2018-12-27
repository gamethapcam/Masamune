package com.quillraven.masamune.ecs.component

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.quillraven.masamune.MainGame

class MoveComponent : ISerializableComponent {
    var speed = 0f

    override fun reset() {
        speed = 0f
    }

    override fun write(json: Json) {
        json.writeValue("speed", speed)
    }

    override fun read(jsonData: JsonValue, game: MainGame) {
        speed = jsonData.getFloat("speed", 0f)
    }
}