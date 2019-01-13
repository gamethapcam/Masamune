package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.ecs.component.InventoryComponent
import com.quillraven.masamune.ecs.component.RemoveComponent

private const val TAG = "InventorySystem"

class InventorySystem constructor(game: MainGame, ecsEngine: ECSEngine) : EntitySystem(), EntityListener {
    private val invCmpMapper = game.cmpMapper.inventory
    private val idCmpMapper = game.cmpMapper.identify
    private val stackCmpMapper = game.cmpMapper.stackable
    private val gameEventManager = game.gameEventManager

    init {
        ecsEngine.addEntityListener(Family.all(InventoryComponent::class.java).get(), this)
        setProcessing(false)
    }

    override fun entityAdded(entity: Entity) {
        // initialize inventory size
        val inventoryCmp = invCmpMapper.get(entity)
        while (inventoryCmp.items.size < inventoryCmp.maxSize) {
            inventoryCmp.items.add(DEFAULT_ENTITY_ID)
        }
    }

    override fun entityRemoved(entity: Entity) {
    }

    fun getInventory(entity: Entity): InventoryComponent? {
        val inventory = invCmpMapper.get(entity)
        if (inventory == null) {
            Gdx.app.error(TAG, "Trying to get an inventory for an entity without inventory component")
        }
        return inventory
    }

    fun addItem(entity: Entity, item: Entity): Boolean {
        val inventory = getInventory(entity) ?: return false
        val stackCmp = stackCmpMapper.get(item)
        if (stackCmp != null) {
            // check if there is already an item of that specific type and increase its stack
            val idCmp = idCmpMapper.get(item)
            for (idx in 0 until inventory.items.size) {
                if (inventory.items[idx] == DEFAULT_ENTITY_ID) continue

                val existingItem = engine.getSystem(IdentifySystem::class.java).getEntityByID(inventory.items[idx])
                if (idCmpMapper.get(existingItem).type == idCmp.type) {
                    // found item of same type --> increase stack
                    stackCmpMapper.get(existingItem).size += stackCmp.size
                    // and remove the item from the game
                    item.add((engine as ECSEngine).createComponent(RemoveComponent::class.java))
                    gameEventManager.dispatchItemSlotUpdated(idx, existingItem)
                    return true
                }
            }
        }

        // find free index
        for (idx in 0 until inventory.items.size) {
            if (inventory.items[idx] == DEFAULT_ENTITY_ID) {
                // found empty slot --> add to inventory by assigning entity ID to the slot
                inventory.items[idx] = idCmpMapper.get(item).id
                gameEventManager.dispatchItemSlotUpdated(idx, item)
                return true
            }
        }

        Gdx.app.debug(TAG, "Inventory is full. Entity cannot pickup more items")
        return false
    }

    fun getInventoryItem(entity: Entity, slotIdx: Int): Entity? {
        val inventory = getInventory(entity)
        if (inventory != null && inventory.items.size > slotIdx) {
            return engine.getSystem(IdentifySystem::class.java).getEntityByID(inventory.items[slotIdx])
        }
        Gdx.app.error(TAG, "Trying to access an item that is not existing in slot $slotIdx")
        return null
    }

    fun moveItem(entity: Entity, fromSlotIdx: Int, toSlotIdx: Int) {
        val inventory = getInventory(entity)
        if (inventory != null) {
            if (inventory.items.size <= fromSlotIdx || inventory.items.size <= toSlotIdx) {
                Gdx.app.error(TAG, "Trying to move items of invalid slots: $fromSlotIdx - $toSlotIdx")
                return
            }

            val itemFrom = inventory.items[fromSlotIdx]
            val itemTo = inventory.items[toSlotIdx]
            inventory.items[fromSlotIdx] = itemTo
            inventory.items[toSlotIdx] = itemFrom

            gameEventManager.dispatchItemSlotUpdated(fromSlotIdx, if (inventory.items[fromSlotIdx] == DEFAULT_ENTITY_ID) null else engine.getSystem(IdentifySystem::class.java).getEntityByID(inventory.items[fromSlotIdx]))
            gameEventManager.dispatchItemSlotUpdated(toSlotIdx, if (inventory.items[toSlotIdx] == DEFAULT_ENTITY_ID) null else engine.getSystem(IdentifySystem::class.java).getEntityByID(inventory.items[toSlotIdx]))
        }
    }
}