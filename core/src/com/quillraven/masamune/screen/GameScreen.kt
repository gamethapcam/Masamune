package com.quillraven.masamune.screen

import com.badlogic.gdx.utils.Json
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.ecs.ECSSerializer
import com.quillraven.masamune.map.EMapType
import com.quillraven.masamune.map.MapManager
import com.quillraven.masamune.map.MapSerializer
import com.quillraven.masamune.ui.GameUI

class GameScreen : Q2DScreen() {
    private val gameUI = GameUI(game.skin, game.gameEventManager)
    private val json = Json()

    init {
        json.setSerializer(MapManager::class.java, MapSerializer(game))
        json.setSerializer(ECSEngine::class.java, ECSSerializer(game))
    }

    override fun hide() {
        stage.clear()
    }

    override fun show() {
        if (game.preferences.contains("mapManager")) {
            json.fromJson(MapManager::class.java, game.preferences.getString("mapManager"))
            if (game.preferences.contains("entities")) {
                json.fromJson(ECSEngine::class.java, game.preferences.getString("entities"))
            } else {
                game.mapManager.loadCharacters()
            }
        } else {
            game.mapManager.setMap(EMapType.MAP01)
            game.mapManager.loadCharacters()
        }
        stage.addActor(gameUI)
    }

    override fun render(delta: Float) {
        game.ecsEngine.update(delta)
        stage.viewport.apply(true)
        stage.act()
        stage.draw()
    }

    override fun pause() {
        game.preferences.putString("mapManager", json.toJson(game.mapManager))
        game.preferences.putString("entities", json.toJson(game.ecsEngine))
        game.preferences.flush()
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