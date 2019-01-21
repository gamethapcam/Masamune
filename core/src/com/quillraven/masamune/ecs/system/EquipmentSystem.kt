package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.ecs.component.EquipmentComponent
import com.quillraven.masamune.event.InputListener
import com.quillraven.masamune.model.EEquipType

private const val TAG = "EquipmentSystem"

class EquipmentSystem constructor(game: MainGame, ecsEngine: ECSEngine) : EntitySystem(), EntityListener, InputListener {
    private val equipCmpMapper = game.cmpMapper.equipment
    private val invCmpMapper = game.cmpMapper.inventory
    private val idCmpMapper = game.cmpMapper.identify
    private val gameEventManager = game.gameEventManager
    private val idSystem by lazy { engine.getSystem(IdentifySystem::class.java) }
    private val inventorySystem by lazy { engine.getSystem(InventorySystem::class.java) }

    init {
        ecsEngine.addEntityListener(Family.all(EquipmentComponent::class.java).get(), this)
        gameEventManager.addInputListener(this)
        setProcessing(false)
    }

    override fun entityAdded(entity: Entity) {
        val equipCmp = equipCmpMapper.get(entity)
        while (equipCmp.equipment.size < EEquipType.values().size) {
            equipCmp.equipment.add(DEFAULT_ENTITY_ID)
        }
    }

    override fun entityRemoved(entity: Entity) {
    }

    override fun inputItemEquipped(inventorySlotIdx: Int, type: EEquipType) {
        equipItem(idSystem.getPlayerEntity(), inventorySlotIdx, type)
    }

    override fun inputItemUnequipped(inventorySlotIdx: Int, type: EEquipType) {
        unequipItem(idSystem.getPlayerEntity(), inventorySlotIdx, type)
    }

    fun getEquipmentItem(entity: Entity, type: EEquipType): Entity? {
        val equipCmp = equipCmpMapper.get(entity)
        if (equipCmp != null && equipCmp.equipment.size <= EEquipType.values().size) {
            if (equipCmp.equipment.items[type.ordinal] == DEFAULT_ENTITY_ID) {
                // empty slot without equipment
                return null
            }
            return idSystem.getEntityByID(equipCmp.equipment.items[type.ordinal])
        }
        Gdx.app.error(TAG, "Trying to access an item that is not existing in equipment $type")
        return null
    }

    fun unequipItem(entity: Entity, inventorySlotIdx: Int, type: EEquipType) {
        val equippedItem = getEquipmentItem(entity, type)
        if (equippedItem == null) {
            Gdx.app.error(TAG, "Trying to unequip an item that does not exist $type")
            return
        }
        val invSlotIdx = inventorySystem.addItem(entity, equippedItem)
        if (invSlotIdx < 0) {
            Gdx.app.debug(TAG, "Cannot unequip because there is no space in the inventory left")
            return
        }
        if (inventorySlotIdx >= 0) {
            inventorySystem.moveItem(entity, invSlotIdx, inventorySlotIdx)
        }
        val equipCmp = equipCmpMapper.get(entity)
        equipCmp.equipment.set(type.ordinal, DEFAULT_ENTITY_ID)
        gameEventManager.dispatchEquipmentSlotUpdated(type, null)
    }

    fun equipItem(entity: Entity, inventorySlotIdx: Int, type: EEquipType) {
        // get item from inventory
        val item = inventorySystem.getInventoryItem(entity, inventorySlotIdx)
        if (item == null) {
            Gdx.app.error(TAG, "Cannot equip a non existing item of slot $inventorySlotIdx")
            return
        }

        val inventory = invCmpMapper.get(entity)
        val equipCmp = equipCmpMapper.get(entity)
        if (equipCmp.equipment.get(type.ordinal) != DEFAULT_ENTITY_ID) {
            // slot already equipped -> try to unequip first and then equip
            unequipItem(entity, -1, type)
        }

        // remove from inventory
        val itemID = inventory.items.get(inventorySlotIdx)
        inventorySystem.removeItem(entity, inventorySlotIdx)
        // set as equipment
        equipCmp.equipment.set(type.ordinal, itemID)
        gameEventManager.dispatchEquipmentSlotUpdated(type, getEquipmentItem(entity, type))
    }
}