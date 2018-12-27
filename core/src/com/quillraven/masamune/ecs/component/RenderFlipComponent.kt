package com.quillraven.masamune.ecs.component

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.quillraven.masamune.MainGame

class RenderFlipComponent : ISerializableComponent {
    var flipCountdown = 0f

    override fun reset() {
        flipCountdown = 0f
    }

    override fun write(json: Json) {
        json.writeValue("ctd", flipCountdown)
    }

    override fun read(jsonData: JsonValue, game: MainGame) {
        flipCountdown = jsonData.getFloat("ctd", 0f)
    }
}