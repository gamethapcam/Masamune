package com.quillraven.masamune.event

import com.badlogic.ashley.signals.Signal

class GameEventManager {
    internal val mapSignal = Signal<MapEvent>()
    internal val mapEvent = MapEvent
}