package com.quillraven.masamune.serialization

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.map.EMapType

class Q2DSerializer constructor(game: MainGame) {
    private val currentMapKey = "currentMap"
    private val mapDataKey = "-data"

    private val json = Json(JsonWriter.OutputType.minimal)
    private val gameStatePreference = Gdx.app.getPreferences("masamune")
    private val ecsEngine = game.ecsEngine
    private val mapManager = game.mapManager

    init {
        json.setSerializer(ECSEngine::class.java, ECSSerializer(game))
    }

    fun saveGameState() {
        gameStatePreference.putString(currentMapKey, mapManager.currentMapType.name)
        gameStatePreference.putString("${mapManager.currentMapType.name}$mapDataKey", json.prettyPrint(ecsEngine))
        gameStatePreference.flush()
    }

    fun loadGameState() {
        val currentMapType = EMapType.valueOf(gameStatePreference.getString(currentMapKey, EMapType.MAP01.name))
        mapManager.setMap(currentMapType)
        val mapDataKey = "${mapManager.currentMapType.name}$mapDataKey"
        if (gameStatePreference.contains(mapDataKey)) {
            json.fromJson(ECSEngine::class.java, gameStatePreference.getString(mapDataKey))
        } else {
            mapManager.loadCharacters()
        }
    }
}