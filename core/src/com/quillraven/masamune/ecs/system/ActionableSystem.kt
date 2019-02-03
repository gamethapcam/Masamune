package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.EntityType
import com.quillraven.masamune.ecs.component.ActionableComponent
import com.quillraven.masamune.ecs.component.Box2DComponent
import com.quillraven.masamune.ecs.component.RemoveComponent
import com.quillraven.masamune.ecs.component.TransformComponent
import com.quillraven.masamune.event.ContactListener
import com.quillraven.masamune.event.InputListener

class ActionableSystem constructor(game: MainGame) : EntitySystem(), ContactListener, InputListener {
    private val actCmpMapper = game.cmpMapper.actionable
    private val idCmpMapper = game.cmpMapper.identify
    private val actionableEntities by lazy { engine.getEntitiesFor(Family.all(ActionableComponent::class.java).exclude(RemoveComponent::class.java).get()) }

    init {
        game.gameEventManager.addContactListener(this)
        game.gameEventManager.addInputListener(this)
        setProcessing(false)
    }

    override fun beginCharacterContact(player: Entity, character: Entity) {
        if (actCmpMapper.get(character) == null) {
            character.add(engine.createComponent(ActionableComponent::class.java).apply { source = player })
        }
    }

    override fun endCharacterContact(player: Entity, character: Entity) {
        if (actCmpMapper.get(character) != null) {
            character.remove(ActionableComponent::class.java)
        }
    }

    override fun beginItemContact(player: Entity, item: Entity) {
        if (actCmpMapper.get(item) == null) {
            item.add(engine.createComponent(ActionableComponent::class.java).apply { source = player })
        }
    }

    override fun endItemContact(player: Entity, item: Entity) {
        if (actCmpMapper.get(item) != null) {
            item.remove(ActionableComponent::class.java)
        }
    }

    override fun inputAction() {
        for (entity in actionableEntities) {
            val actionableCmp = actCmpMapper.get(entity)
            val idCmp = idCmpMapper.get(entity)
            if (idCmp.entityType == EntityType.ITEM) {
                // item map interaction
                if (engine.getSystem(InventorySystem::class.java).addItem(actionableCmp.source, entity) >= 0) {
                    // remove transform component so that it no longer gets rendered on the map
                    entity.remove(TransformComponent::class.java)
                    // remove box2d component to remove collision body
                    entity.remove(Box2DComponent::class.java)
                    entity.remove(ActionableComponent::class.java)
                }
            }

            break
        }
    }
}