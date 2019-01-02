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

class ActionableSystem constructor(game: MainGame, private val ecsEngine : ECSEngine) : IteratingSystem(Family.all(ActionableComponent::class.java).get()), Listener<ContactEvent> {
    private val actCmpMapper = game.cmpMapper.actionable

    init {
        game.gameEventManager.addContactEventListener(this)
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
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
}