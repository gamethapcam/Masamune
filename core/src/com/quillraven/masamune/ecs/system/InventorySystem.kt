package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.component.InventoryComponent

private const val TAG = "InventorySystem"

class InventorySystem constructor(game: MainGame) : IteratingSystem(Family.all(InventoryComponent::class.java).get()) {
    private val invCmpMapper = game.cmpMapper.inventory
    private val idCmpMapper = game.cmpMapper.identify
    private val gameEventManager = game.gameEventManager

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val inventoryCmp = invCmpMapper.get(entity)
        if (inventoryCmp.items.size != inventoryCmp.maxSize) {
            // add inventory slots
            while (inventoryCmp.items.size < inventoryCmp.maxSize) {
                inventoryCmp.items.add(-1)
            }
            //TODO remove inventory slots: move items to other slots or drop on map
        }
    }

    fun getInventory(entity: Entity): InventoryComponent? {
        val inventory = invCmpMapper.get(entity)
        if (inventory == null) {
            Gdx.app.error(TAG, "Trying to get an inventory for an entity without inventory component")
        }
        return inventory
    }

    fun addItem(entity: Entity, item: Entity): Boolean {
        val inventoryCmp = invCmpMapper.get(entity)
        if (inventoryCmp == null) {
            Gdx.app.error(TAG, "Entity tries to pickup an item without having an inventory")
            return false
        }

        // find free index
        for (idx in 0 until inventoryCmp.maxSize) {
            if (inventoryCmp.items[idx] == -1) {
                // add to inventory
                inventoryCmp.items[idx] = idCmpMapper.get(item).id
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

            gameEventManager.dispatchItemSlotUpdated(fromSlotIdx, if (inventory.items[fromSlotIdx] == -1) null else engine.getSystem(IdentifySystem::class.java).getEntityByID(inventory.items[fromSlotIdx]))
            gameEventManager.dispatchItemSlotUpdated(toSlotIdx, if (inventory.items[toSlotIdx] == -1) null else engine.getSystem(IdentifySystem::class.java).getEntityByID(inventory.items[toSlotIdx]))
        }
    }
}