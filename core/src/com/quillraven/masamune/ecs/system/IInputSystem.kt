package com.quillraven.masamune.ecs.system

import com.quillraven.masamune.event.InputEvent


interface IInputSystem {
    fun handleInputEvent(event: InputEvent)
}