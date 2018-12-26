package com.quillraven.masamune.event

enum class EInputType {
    MOVE
}

object InputEvent {
    internal var type: EInputType = EInputType.MOVE
    internal var movePercX = 0f
    internal var movePercY = 0f
}