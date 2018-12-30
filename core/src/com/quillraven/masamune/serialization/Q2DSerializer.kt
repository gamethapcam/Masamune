package com.quillraven.masamune.serialization

import com.badlogic.gdx.Gdx
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.map.EMapType

class Q2DSerializer constructor(game: MainGame) {
    private val currentMapKey = "currentMap"
    private val mapDataKey = "-data"

    private val json = game.json
    private val gameStatePreference = Gdx.app.getPreferences("masamune")
    private val ecsEngine by lazy { game.ecsEngine }
    private val mapManager by lazy { game.mapManager }

    init {
        json.setSerializer(ECSEngine::class.java, ECSSerializer(game))
    }

    fun saveGameState() {
        gameStatePreference.putString(currentMapKey, mapManager.currentMapType.name)
        gameStatePreference.putString("${mapManager.currentMapType.name}$mapDataKey", json.toJson(ecsEngine))
        gameStatePreference.flush()
    }

    fun loadGameState() {
        val currentMapType = EMapType.valueOf(gameStatePreference.getString(currentMapKey, EMapType.MAP01.name))
        mapManager.setMap(currentMapType)
    }

    fun loadMapEntities(mapType: EMapType) {
        val mapDataKey = "${mapType.name}$mapDataKey"
        if (gameStatePreference.contains(mapDataKey)) {
            json.fromJson(ECSEngine::class.java, gameStatePreference.getString(mapDataKey))
        } else {
            mapManager.loadCharacters()
        }
    }
}