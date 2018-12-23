package com.quillraven.masamune.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.tiled.TiledMap
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.UNIT_SCALE

private const val TAG = "MapManager"

class MapManager constructor(game: MainGame) {
    private val ecsEngine = game.ecsEngine
    private val assetManger = game.assetManager
    private val gameEventManager = game.gameEventManager

    fun setMap(type: EMapType) {
        val tiledMap = assetManger.get(type.filePath, TiledMap::class.java)

        loadCharacters(tiledMap)

        gameEventManager.mapEvent.newTiledMap = tiledMap
        gameEventManager.mapSignal.dispatch(gameEventManager.mapEvent)
    }

    private fun loadCharacters(tiledMap: TiledMap) {
        val mapLayer = tiledMap.layers.get("characters")
        for (mapObj in mapLayer.objects) {
            val charType = mapObj.properties.get("type", "", String::class.java)
            if (charType.isBlank()) {
                Gdx.app.debug(TAG, "Type is not defined for character tile ${mapObj.properties.get("id", Int::class.java)}")
                continue
            }

            if ("PLAYER" == charType) {
                ecsEngine.createPlayer(mapObj.properties.get("x", 0f, Float::class.java) * UNIT_SCALE, mapObj.properties.get("y", 0f, Float::class.java) * UNIT_SCALE)
            }
        }
    }
}