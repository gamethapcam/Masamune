package com.quillraven.masamune.screen

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.ObjectMap
import com.quillraven.masamune.CharacterCfg
import com.quillraven.masamune.map.EMapType

class LoadingScreen : Q2DScreen() {
    private val assetManager = game.assetManager

    override fun hide() {

    }

    override fun show() {
        assetManager.load("textures.atlas", TextureAtlas::class.java)

        for (mapType in EMapType.values()) {
            assetManager.load(mapType.filePath, TiledMap::class.java)
        }
    }

    override fun render(delta: Float) {
        if (assetManager.update()) {
            if (game.characterCfgMap.isEmpty) {
                loadCharacterConfigurations(game.characterCfgMap)
            }
            game.setScreen(GameScreen::class.java, true)
        }
    }

    private fun loadCharacterConfigurations(characterCfgMap: ObjectMap<String, CharacterCfg>) {
        val jsonValue = JsonReader().parse(assetManager.fileHandleResolver.resolve("cfg/character.json"))

        var entry = jsonValue.child
        while (entry != null) {
            characterCfgMap.put(entry.name, CharacterCfg(
                    entry.name,
                    if (entry.getString("type") == "dynamic") BodyDef.BodyType.DynamicBody else BodyDef.BodyType.StaticBody,
                    entry.getString("texture"),
                    entry.getFloat("width", 1f),
                    entry.getFloat("height", 1f),
                    entry.getFloat("speed", 0f),
                    entry.getBoolean("flip", true)))
            entry = entry.next
        }
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun resize(width: Int, height: Int) {

    }

    override fun dispose() {

    }
}