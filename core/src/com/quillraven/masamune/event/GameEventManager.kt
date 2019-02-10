package com.quillraven.masamune.event

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.utils.Array
import com.quillraven.masamune.map.EMapType
import com.quillraven.masamune.model.EAttributeType
import com.quillraven.masamune.model.EEquipType

class GameEventManager {
    private val mapEvent = MapEvent()
    private val mapListeners = Array<MapListener>()

    private val inputListeners = Array<InputListener>()

    private val contactListeners = Array<ContactListener>()

    private val itemListeners = Array<ItemListener>()

    private val attributeListeners = Array<AttributeListener>()

    fun addMapListener(listener: MapListener) {
        mapListeners.add(listener)
    }

    fun dispatchMapChanged(oldType: EMapType, newType: EMapType, map: TiledMap, width: Float, height: Float, bgdLayers: Array<TiledMapTileLayer>, fgdLayers: Array<TiledMapTileLayer>) {
        mapEvent.apply {
            this.oldType = oldType
            this.newType = newType
            this.map = map
            this.width = width
            this.height = height
            this.bgdLayers = bgdLayers
            this.fgdLayers = fgdLayers
        }
        for (listener in mapListeners) {
            listener.mapChanged(mapEvent)
        }
    }

    fun addInputListener(listener: InputListener) {
        inputListeners.add(listener)
    }

    fun dispatchInputMoveEvent(percentX: Float, percentY: Float) {
        for (listener in inputListeners) {
            listener.inputMove(percentX, percentY)
        }
    }

    fun dispatchInputActionEvent() {
        for (listener in inputListeners) {
            listener.inputAction()
        }
    }

    fun dispatchInputShowInventory() {
        for (listener in inputListeners) {
            listener.inputShowInventory()
        }
    }

    fun dispatchInputShowStats() {
        for (listener in inputListeners) {
            listener.inputShowStats()
        }
    }

    fun dispatchInputShowInventoryItem(slotIdx: Int) {
        for (listener in inputListeners) {
            listener.inputShowInventoryItem(slotIdx)
        }
    }

    fun dispatchInputShowEquipmentItem(type: EEquipType) {
        for (listener in inputListeners) {
            listener.inputShowEquipmentItem(type)
        }
    }

    fun dispatchInputItemMove(fromSlotIdx: Int, toSlotIdx: Int) {
        for (listener in inputListeners) {
            listener.inputItemMoved(fromSlotIdx, toSlotIdx)
        }
    }

    fun dispatchInputItemEquip(inventorySlotIdx: Int, type: EEquipType) {
        for (listener in inputListeners) {
            listener.inputItemEquipped(inventorySlotIdx, type)
        }
    }

    fun dispatchInputItemUnequip(inventorySlotIdx: Int, type: EEquipType) {
        for (listener in inputListeners) {
            listener.inputItemUnequipped(inventorySlotIdx, type)
        }
    }

    fun dispatchInputUseItem(inventorySlotIdx: Int) {
        for (listener in inputListeners) {
            listener.inputUseItem(inventorySlotIdx)
        }
    }

    fun addContactListener(listener: ContactListener) {
        contactListeners.add(listener)
    }

    fun dispatchContactBeginCharacter(player: Entity, character: Entity) {
        for (listener in contactListeners) {
            listener.beginCharacterContact(player, character)
        }
    }

    fun dispatchContactEndCharacter(player: Entity, character: Entity) {
        for (listener in contactListeners) {
            listener.endCharacterContact(player, character)
        }
    }

    fun dispatchContactBeginItem(player: Entity, item: Entity) {
        for (listener in contactListeners) {
            listener.beginCharacterContact(player, item)
        }
    }

    fun dispatchContactEndItem(player: Entity, item: Entity) {
        for (listener in contactListeners) {
            listener.endCharacterContact(player, item)
        }
    }

    fun addItemListener(listener: ItemListener) {
        itemListeners.add(listener)
    }

    fun dispatchInventorySlotUpdated(slotIdx: Int, item: Entity?) {
        for (listener in itemListeners) {
            listener.inventorySlotUpdated(slotIdx, item)
        }
    }

    fun dispatchInventoryResize(newSize: Int) {
        for (listener in itemListeners) {
            listener.inventoryResize(newSize)
        }
    }

    fun dispatchEquipmentSlotUpdated(entity: Entity, type: EEquipType, prevItem: Entity?, newItem: Entity?) {
        for (listener in itemListeners) {
            listener.equipSlotUpdated(entity, type, prevItem, newItem)
        }
    }

    fun dispatchUseItem(entity: Entity, item: Entity) {
        for (listener in itemListeners) {
            listener.useItem(entity, item)
        }
    }

    fun addAttributeListener(listener: AttributeListener) {
        attributeListeners.add(listener)
    }

    fun dispatchAttributeUpdated(entity: Entity, type: EAttributeType, newValue: Float) {
        for (listener in attributeListeners) {
            listener.attributeUpdated(entity, type, newValue)
        }
    }
}