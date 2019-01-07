package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.ecs.EntityType
import com.quillraven.masamune.ecs.component.ActionableComponent
import com.quillraven.masamune.ecs.component.Box2DComponent
import com.quillraven.masamune.ecs.component.TransformComponent
import com.quillraven.masamune.event.ContactListener
import com.quillraven.masamune.event.InputListener

class ActionableSystem constructor(game: MainGame, private val ecsEngine: ECSEngine) : IteratingSystem(Family.all(ActionableComponent::class.java).get()), ContactListener, InputListener {
    private val actCmpMapper = game.cmpMapper.actionable
    private val idCmpMapper = game.cmpMapper.identify
    private var process = false

    init {
        game.gameEventManager.addContactListener(this)
        game.gameEventManager.addInputListener(this)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (process) {
            val actionableCmp = actCmpMapper.get(entity)
            val idCmp = idCmpMapper.get(entity)
            if (idCmp.entityType == EntityType.ITEM) {
                // item map interaction
                if (engine.getSystem(InventorySystem::class.java).addItem(actionableCmp.source, entity)) {
                    // remove transform component so that it no longer gets rendered on the map
                    entity.remove(TransformComponent::class.java)
                    // remove box2d component to remove collision body
                    entity.remove(Box2DComponent::class.java)
                }
            }

            process = false
        }
    }

    override fun beginCharacterContact(player: Entity, character: Entity) {
        if (actCmpMapper.get(character) == null) {
            character.add(ecsEngine.createComponent(ActionableComponent::class.java).apply { source = player })
        }
    }

    override fun endCharacterContact(player: Entity, character: Entity) {
        if (actCmpMapper.get(character) != null) {
            character.remove(ActionableComponent::class.java)
        }
    }

    override fun beginItemContact(player: Entity, item: Entity) {
        if (actCmpMapper.get(item) == null) {
            item.add(ecsEngine.createComponent(ActionableComponent::class.java).apply { source = player })
        }
    }

    override fun endItemContact(player: Entity, item: Entity) {
        if (actCmpMapper.get(item) != null) {
            item.remove(ActionableComponent::class.java)
        }
    }

    override fun inputAction() {
        process = true
    }
}