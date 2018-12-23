package com.quillraven.masamune.screen

import com.quillraven.masamune.map.EMapType

class GameScreen : Q2DScreen() {
    override fun hide() {

    }

    override fun show() {
        game.mapManager.setMap(EMapType.MAP01)
    }

    override fun render(delta: Float) {
        game.ecsEngine.update(delta)
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun resize(width: Int, height: Int) {
        game.gameViewPort.update(width, height, true)
    }

    override fun dispose() {

    }
}