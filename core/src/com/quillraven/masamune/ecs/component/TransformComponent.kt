package com.quillraven.masamune.ecs.component

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.quillraven.masamune.MainGame

class TransformComponent : ISerializableComponent {
    var width = 0f
    var height = 0f

    var x = 0f
    var y = 0f
    var prevX = 0f
    var prevY = 0f
    var interpolatedX = 0f
    var interpolatedY = 0f

    var angle = 0f
    var prevAngle = 0f
    var interpolatedAngle = 0f

    override fun write(json: Json) {
        json.writeValue("width", width)
        json.writeValue("height", height)
        json.writeValue("x", x)
        json.writeValue("y", y)
        json.writeValue("angle", angle)
    }

    override fun read(jsonData: JsonValue, game: MainGame) {
        width = jsonData.getFloat("width", 1f)
        height = jsonData.getFloat("height", 1f)
        x = jsonData.getFloat("x", 0f)
        y = jsonData.getFloat("y", 0f)
        angle = jsonData.getFloat("angle", 0f)
    }

    override fun reset() {
        width = 0f
        height = 0f

        x = 0f
        y = 0f
        prevX = 0f
        prevY = 0f
        interpolatedX = 0f
        interpolatedY = 0f

        angle = 0f
        prevAngle = 0f
        interpolatedAngle = 0f
    }
}