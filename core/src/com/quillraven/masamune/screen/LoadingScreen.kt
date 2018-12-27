package com.quillraven.masamune.screen

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.quillraven.masamune.map.EMapType
import com.quillraven.masamune.model.CharacterCfgLoader
import com.quillraven.masamune.model.CharacterCfgMap

class LoadingScreen : Q2DScreen() {
    private val assetManager = game.assetManager

    override fun hide() {

    }

    override fun show() {
        assetManager.load("textures.atlas", TextureAtlas::class.java)

        assetManager.setLoader(CharacterCfgMap::class.java, CharacterCfgLoader(assetManager.fileHandleResolver))
        assetManager.load("cfg/character.json", CharacterCfgMap::class.java)

        assetManager.setLoader(TiledMap::class.java, TmxMapLoader(assetManager.fileHandleResolver))
        for (mapType in EMapType.values()) {
            assetManager.load(mapType.filePath, TiledMap::class.java)
        }
    }

    override fun render(delta: Float) {
        if (assetManager.update()) {
            game.setScreen(GameScreen::class.java, true)
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