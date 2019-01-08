package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.ecs.component.InventoryComponent

private const val TAG = "InventorySystem"

class InventorySystem constructor(game: MainGame, ecsEngine: ECSEngine) : EntitySystem(), EntityListener {
    private val invCmpMapper = game.cmpMapper.inventory
    private val idCmpMapper = game.cmpMapper.identify
    private val gameEventManager = game.gameEventManager

    init {
        ecsEngine.addEntityListener(Family.all(InventoryComponent::class.java).get(), this)
        setProcessing(false)
    }

    override fun entityAdded(entity: Entity) {
        // initialize inventory size
        val inventoryCmp = invCmpMapper.get(entity)
        while (inventoryCmp.items.size < inventoryCmp.maxSize) {
            inventoryCmp.items.add(-1)
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

        // find free index
        for (idx in 0 until inventory.items.size) {
            if (inventory.items[idx] == -1) {
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

            gameEventManager.dispatchItemSlotUpdated(fromSlotIdx, if (inventory.items[fromSlotIdx] == -1) null else engine.getSystem(IdentifySystem::class.java).getEntityByID(inventory.items[fromSlotIdx]))
            gameEventManager.dispatchItemSlotUpdated(toSlotIdx, if (inventory.items[toSlotIdx] == -1) null else engine.getSystem(IdentifySystem::class.java).getEntityByID(inventory.items[toSlotIdx]))
        }
    }
}