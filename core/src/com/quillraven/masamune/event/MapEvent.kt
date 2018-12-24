package com.quillraven.masamune.event

import com.badlogic.gdx.maps.tiled.TiledMap
import com.quillraven.masamune.map.EMapType

object MapEvent {
    internal lateinit var type: EMapType
    internal lateinit var map: TiledMap
    internal var width = 0f
    internal var height = 0f
}