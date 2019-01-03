package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.ecs.component.ActionableComponent
import com.quillraven.masamune.event.ContactListener
import com.quillraven.masamune.event.InputListener

class ActionableSystem constructor(game: MainGame, private val ecsEngine: ECSEngine) : IteratingSystem(Family.all(ActionableComponent::class.java).get()), ContactListener, InputListener {
    private val actCmpMapper = game.cmpMapper.actionable
    private var process = false

    init {
        game.gameEventManager.addContactListener(this)
        game.gameEventManager.addInputListener(this)
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        if (process) {
            //TODO trigger logic of entity that should happen when player interacts with it
            process = false
        }
    }

    override fun beginCharacterContact(player: Entity, character: Entity) {
        if (actCmpMapper.get(character) == null) {
            character.add(ecsEngine.createComponent(ActionableComponent::class.java))
        }
    }

    override fun endCharacterContact(player: Entity, character: Entity) {
        if (actCmpMapper.get(character) != null) {
            character.remove(ActionableComponent::class.java)
        }
    }

    override fun inputMove(percentX: Float, percentY: Float) {
        // not needed for this system
    }

    override fun inputAction() {
        process = true
    }
}