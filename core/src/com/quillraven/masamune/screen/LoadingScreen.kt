package com.quillraven.masamune.screen

import com.badlogic.gdx.maps.tiled.TiledMap
import com.quillraven.masamune.map.EMapType

class LoadingScreen : Q2DScreen() {
    private val assetManager = game.assetManager

    override fun hide() {

    }

    override fun show() {
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