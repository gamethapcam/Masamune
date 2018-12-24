package com.quillraven.masamune.event

import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import com.badlogic.gdx.maps.tiled.TiledMap
import com.quillraven.masamune.map.EMapType

class GameEventManager {
    private val mapSignal = Signal<MapEvent>()
    private val mapEvent = MapEvent

    fun addMapEventListener(listener: Listener<MapEvent>) {
        mapSignal.add(listener)
    }

    fun dispatchMapEvent(type: EMapType, map: TiledMap, width: Float, height: Float) {
        mapEvent.type = type
        mapEvent.map = map
        mapEvent.width = width
        mapEvent.height = height
        mapSignal.dispatch(mapEvent)
    }
}