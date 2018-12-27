package com.quillraven.masamune.ecs.component

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.quillraven.masamune.MainGame

class RenderComponent : ISerializableComponent {
    lateinit var sprite: Sprite
    var texturePath = ""
    var flipX = false
    var flipY = false
    var width = 1f
    var height = 1f

    override fun reset() {
        texturePath = ""
        flipX = false
        flipY = false
        width = 1f
        height = 1f
    }

    override fun write(json: Json) {
        json.writeValue("texture", texturePath)
        json.writeValue("flipX", flipX)
        json.writeValue("flipY", flipY)
        json.writeValue("width", width)
        json.writeValue("height", height)
    }

    override fun read(jsonData: JsonValue, game: MainGame) {
        texturePath = jsonData.getString("texture")
        flipX = jsonData.getBoolean("flipX", false)
        flipY = jsonData.getBoolean("flipY", false)
        width = jsonData.getFloat("width", 1f)
        height = jsonData.getFloat("height", 1f)
        sprite = game.spriteCache.getSprite(texturePath)
    }
}