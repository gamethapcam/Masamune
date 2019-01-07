package com.quillraven.masamune.event

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.utils.Array
import com.quillraven.masamune.map.EMapType

class GameEventManager {
    private val mapEvent = MapEvent
    private val mapListeners = Array<MapListener>()

    private val inputListeners = Array<InputListener>()

    private val contactListeners = Array<ContactListener>()

    private val itemListeners = Array<ItemListener>()

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

    fun dispatchInputShowItem(slotIdx: Int) {
        for (listener in inputListeners) {
            listener.inputShowItem(slotIdx)
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

    fun dispatchItemMove(fromSlotIdx: Int, toSlotIdx: Int) {
        for (listener in itemListeners) {
            listener.itemMoved(fromSlotIdx, toSlotIdx)
        }
    }

    fun dispatchItemSlotUpdated(slotIdx: Int, item: Entity?) {
        for (listener in itemListeners) {
            listener.itemSlotUpdated(slotIdx, item)
        }
    }
}