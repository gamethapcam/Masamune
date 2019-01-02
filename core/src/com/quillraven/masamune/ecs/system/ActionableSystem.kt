package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import com.badlogic.ashley.systems.IteratingSystem
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.ecs.component.ActionableComponent
import com.quillraven.masamune.event.ContactEvent
import com.quillraven.masamune.event.EInputType
import com.quillraven.masamune.event.InputEvent

class ActionableSystem constructor(game: MainGame, private val ecsEngine: ECSEngine) : IteratingSystem(Family.all(ActionableComponent::class.java).get()), Listener<ContactEvent>, IInputSystem {
    private val actCmpMapper = game.cmpMapper.actionable
    private var process = false

    init {
        game.gameEventManager.addContactEventListener(this)
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        if (process) {
            //TODO trigger logic of entity that should happen when player interacts with it
            process = false
        }
    }

    override fun receive(signal: Signal<ContactEvent>, obj: ContactEvent) {
        val player = obj.player
        val character = obj.character

        if (player != null && character != null) {
            if (obj.endContact && actCmpMapper.get(character) != null) {
                character.remove(ActionableComponent::class.java)
            } else if (!obj.endContact && actCmpMapper.get(character) == null) {
                character.add(ecsEngine.createComponent(ActionableComponent::class.java))
            }
        }
    }

    override fun handleInputEvent(event: InputEvent) {
        if (event.type == EInputType.ACTION) {
            process = true
        }
    }
}