package com.quillraven.masamune.event

import com.badlogic.gdx.maps.tiled.TiledMap
import com.quillraven.masamune.map.EMapType

object MapEvent {
    internal var oldType = EMapType.UNDEFINED
    internal var newType = EMapType.UNDEFINED
    internal lateinit var map: TiledMap
    internal var width = 0f
    internal var height = 0f
}