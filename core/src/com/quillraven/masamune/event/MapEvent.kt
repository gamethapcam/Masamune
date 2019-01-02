package com.quillraven.masamune.event

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.utils.Array
import com.quillraven.masamune.map.EMapType

object MapEvent {
    internal var oldType = EMapType.UNDEFINED
    internal var newType = EMapType.UNDEFINED
    internal lateinit var map: TiledMap
    internal var width = 0f
    internal var height = 0f
    internal lateinit var bgdLayers: Array<TiledMapTileLayer>
    internal lateinit var fgdLayers: Array<TiledMapTileLayer>
}