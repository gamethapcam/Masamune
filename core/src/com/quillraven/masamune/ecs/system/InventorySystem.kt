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
import com.quillraven.masamune.event.InputListener

private const val TAG = "InventorySystem"

class InventorySystem constructor(game: MainGame, private val ecsEngine: ECSEngine) : EntitySystem(), EntityListener, InputListener {
    private val invCmpMapper = game.cmpMapper.inventory
    private val idCmpMapper = game.cmpMapper.identify
    private val stackCmpMapper = game.cmpMapper.stackable
    private val consumableCmpMapper = game.cmpMapper.consumable
    private val gameEventManager = game.gameEventManager
    private val idSystem by lazy { engine.getSystem(IdentifySystem::class.java) }

    init {
        ecsEngine.addEntityListener(Family.all(InventoryComponent::class.java).get(), this)
        gameEventManager.addInputListener(this)
        setProcessing(false)
    }

    override fun entityAdded(entity: Entity) {
        // initialize inventory size
        val inventoryCmp = invCmpMapper.get(entity)
        resizeInventory(entity, inventoryCmp.maxSize)
    }

    override fun entityRemoved(entity: Entity) {
    }

    override fun inputItemMoved(fromSlotIdx: Int, toSlotIdx: Int) {
        moveItem(idSystem.getPlayerEntity(), fromSlotIdx, toSlotIdx)
    }

    override fun inputUseItem(inventorySlotIdx: Int) {
        val item = getInventoryItem(idSystem.getPlayerEntity(), inventorySlotIdx)
        if (item == null) {
            Gdx.app.error(TAG, "Trying to use a non-existing item in slot $inventorySlotIdx")
            return
        }

        if (consumableCmpMapper.get(item) == null) {
            Gdx.app.debug(TAG, "Cannot use a non-consumable item")
            return
        }

        gameEventManager.dispatchUseItem(idSystem.getPlayerEntity(), item)
        val stackCmp = stackCmpMapper.get(item)
        if (stackCmp != null && stackCmp.size > 1) {
            // reduce stack
            --stackCmp.size
            gameEventManager.dispatchInventorySlotUpdated(inventorySlotIdx, item)
            return
        }

        // remove item
        removeItem(idSystem.getPlayerEntity(), inventorySlotIdx)
        item.add(ecsEngine.createComponent(RemoveComponent::class.java))
    }

    private fun resizeInventory(entity: Entity, newSize: Int) {
        val inventoryCmp = invCmpMapper.get(entity)
        inventoryCmp.maxSize = newSize
        while (inventoryCmp.items.size < inventoryCmp.maxSize) {
            inventoryCmp.items.add(DEFAULT_ENTITY_ID)
        }
        if (entity == engine.getSystem(IdentifySystem::class.java).getPlayerEntity()) {
            gameEventManager.dispatchInventoryResize(inventoryCmp.maxSize)
        }
    }

    fun addItem(entity: Entity, item: Entity): Int {
        val inventory = invCmpMapper.get(entity)
        val stackCmp = stackCmpMapper.get(item)
        if (stackCmp != null) {
            // check if there is already an item of that specific type and increase its stack
            val idCmp = idCmpMapper.get(item)
            for (idx in 0 until inventory.items.size) {
                if (inventory.items[idx] == DEFAULT_ENTITY_ID) continue

                val existingItem = idSystem.getEntityByID(inventory.items[idx])
                if (idCmpMapper.get(existingItem).type == idCmp.type) {
                    // found item of same type --> increase stack
                    stackCmpMapper.get(existingItem).size += stackCmp.size
                    // and remove the item from the game because it is part of the stack
                    item.add((engine as ECSEngine).createComponent(RemoveComponent::class.java))
                    gameEventManager.dispatchInventorySlotUpdated(idx, existingItem)
                    return idx
                }
            }
        }

        // find free index
        for (idx in 0 until inventory.items.size) {
            if (inventory.items[idx] == DEFAULT_ENTITY_ID) {
                // found empty slot --> add to inventory by assigning entity ID to the slot
                inventory.items[idx] = idCmpMapper.get(item).id
                gameEventManager.dispatchInventorySlotUpdated(idx, item)
                return idx
            }
        }

        Gdx.app.debug(TAG, "Inventory is full. Entity cannot pickup more items")
        return -1
    }

    fun getInventoryItem(entity: Entity, slotIdx: Int): Entity? {
        val inventory = invCmpMapper.get(entity)
        if (inventory != null && inventory.items.size > slotIdx) {
            if (inventory.items[slotIdx] == DEFAULT_ENTITY_ID) {
                // empty slot without item
                return null
            }
            return idSystem.getEntityByID(inventory.items[slotIdx])
        }
        Gdx.app.error(TAG, "Trying to access an item that is not existing in slot $slotIdx")
        return null
    }

    fun moveItem(entity: Entity, fromSlotIdx: Int, toSlotIdx: Int) {
        val inventory = invCmpMapper.get(entity)
        if (inventory.items.size <= fromSlotIdx || inventory.items.size <= toSlotIdx) {
            Gdx.app.error(TAG, "Trying to move items of invalid slots: $fromSlotIdx - $toSlotIdx")
            return
        }

        val itemFrom = inventory.items[fromSlotIdx]
        val itemTo = inventory.items[toSlotIdx]
        inventory.items[fromSlotIdx] = itemTo
        inventory.items[toSlotIdx] = itemFrom

        gameEventManager.dispatchInventorySlotUpdated(fromSlotIdx, getInventoryItem(entity, fromSlotIdx))
        gameEventManager.dispatchInventorySlotUpdated(toSlotIdx, getInventoryItem(entity, toSlotIdx))
    }

    fun removeItem(entity: Entity, slotIdx: Int) {
        invCmpMapper.get(entity).items.set(slotIdx, DEFAULT_ENTITY_ID)
        gameEventManager.dispatchInventorySlotUpdated(slotIdx, getInventoryItem(entity, slotIdx))
    }
}