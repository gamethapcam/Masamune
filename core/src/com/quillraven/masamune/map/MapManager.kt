package com.quillraven.masamune.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.tiled.TiledMap
import com.quillraven.masamune.MainGame

class MapManager {
    private val assetManger = (Gdx.app.applicationListener as MainGame).assetManager
    private val gameEventManager = (Gdx.app.applicationListener as MainGame).gameEventManager

    fun setMap(type: EMapType) {
        val tiledMap = assetManger.get(type.filePath, TiledMap::class.java)

        gameEventManager.mapEvent.newTiledMap = tiledMap
        gameEventManager.mapSignal.dispatch(gameEventManager.mapEvent)
    }
}