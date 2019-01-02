package com.quillraven.masamune

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.ObjectMap

private const val TAG = "SpriteCache"

class SpriteCache constructor(game: MainGame) {
    private val cache = ObjectMap<String, Sprite>()
    private val texAtlas = game.assetManager.get("textures.atlas", TextureAtlas::class.java)
    internal val texWidth = getDefaultSprite().texture.width
    internal val texHeight = getDefaultSprite().texture.height

    fun getSprite(texture: String): Sprite {
        var sprite = cache.get(texture)
        if (sprite == null) {
            Gdx.app.debug(TAG, "Creating sprite $texture")
            sprite = texAtlas.createSprite(texture)
            if (sprite == null) {
                Gdx.app.error(TAG, "Could not find texture region $texture. Using default sprite instead")
                return getDefaultSprite()
            }
            cache.put(texture, sprite)
        }
        return sprite
    }

    private fun getDefaultSprite(): Sprite {
        var defaultSprite = cache.get("frederick_new")
        if (defaultSprite == null) {
            Gdx.app.debug(TAG, "Creating default sprite")
            defaultSprite = texAtlas.createSprite("frederick_new")
            cache.put("frederick_new", defaultSprite)
        }
        return defaultSprite
    }
}