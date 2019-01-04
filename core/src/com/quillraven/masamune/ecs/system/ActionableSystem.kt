package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.ecs.component.ActionableComponent
import com.quillraven.masamune.ecs.component.Box2DComponent
import com.quillraven.masamune.ecs.component.TransformComponent
import com.quillraven.masamune.event.ContactListener
import com.quillraven.masamune.event.InputListener

private const val TAG = "ActionableSystem"

class ActionableSystem constructor(game: MainGame, private val ecsEngine: ECSEngine) : IteratingSystem(Family.all(ActionableComponent::class.java).get()), ContactListener, InputListener {
    private val actCmpMapper = game.cmpMapper.actionable
    private val itemCmpMapper = game.cmpMapper.item
    private val inventoryCmpMapper = game.cmpMapper.inventory
    private var process = false

    init {
        game.gameEventManager.addContactListener(this)
        game.gameEventManager.addInputListener(this)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (process) {
            val actionableCmp = actCmpMapper.get(entity)
            val itemCmp = itemCmpMapper.get(entity)
            if (itemCmp != null) {
                // item map interaction
                val inventoryCmp = inventoryCmpMapper.get(actionableCmp.source)
                if (inventoryCmp == null) {
                    Gdx.app.error(TAG, "Entity tries to pickup an item without having an inventory")
                    return
                }
                if (inventoryCmp.items.size >= inventoryCmp.maxSize) {
                    Gdx.app.debug(TAG, "Inventory is full. Entity cannot pickup more items")
                    return
                }

                // remove transform component so that it no longer gets rendered on the map
                entity.remove(TransformComponent::class.java)
                // remove box2d component to remove collision body
                entity.remove(Box2DComponent::class.java)
                // add to inventory
                inventoryCmp.items.add(entity)
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

    override fun inputMove(percentX: Float, percentY: Float) {
        // not needed for this system
    }

    override fun inputAction() {
        process = true
    }
}