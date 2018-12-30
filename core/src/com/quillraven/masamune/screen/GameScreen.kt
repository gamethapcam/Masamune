package com.quillraven.masamune.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.quillraven.masamune.map.EMapType
import com.quillraven.masamune.ui.GameUI

class GameScreen : Q2DScreen() {
    private val gameUI = GameUI(game.skin, game.gameEventManager)

    override fun hide() {
        stage.clear()
    }

    override fun show() {
        game.serializer.loadGameState()
        stage.addActor(gameUI)
    }

    override fun render(delta: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
            game.mapManager.setMap(EMapType.MAP01)
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            game.mapManager.setMap(EMapType.MAP02)
        }

        game.ecsEngine.update(delta)
        stage.viewport.apply(true)
        stage.act()
        stage.draw()
    }

    override fun pause() {
        game.serializer.saveGameState()
    }

    override fun resume() {

    }

    override fun resize(width: Int, height: Int) {
        game.gameViewPort.update(width, height, true)
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {

    }
}