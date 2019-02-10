package com.quillraven.masamune.screen

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.utils.StringBuilder
import com.quillraven.masamune.ecs.system.EquipmentSystem
import com.quillraven.masamune.ecs.system.IdentifySystem
import com.quillraven.masamune.ecs.system.InventorySystem
import com.quillraven.masamune.event.AttributeListener
import com.quillraven.masamune.event.InputListener
import com.quillraven.masamune.event.ItemListener
import com.quillraven.masamune.map.EMapType
import com.quillraven.masamune.model.EAttributeType
import com.quillraven.masamune.model.EEquipType
import com.quillraven.masamune.ui.GameUI

class GameScreen : Q2DScreen(), InputListener, ItemListener, AttributeListener {
    private val gameUI = GameUI(game)
    private val inventorySystem by lazy { game.ecsEngine.getSystem(InventorySystem::class.java) }
    private val equipSystem by lazy { game.ecsEngine.getSystem(EquipmentSystem::class.java) }
    private val idSystem by lazy { game.ecsEngine.getSystem(IdentifySystem::class.java) }
    private val strBuffer = StringBuilder(0)

    init {
        game.gameEventManager.addInputListener(this)
        game.gameEventManager.addItemListener(this)
        game.gameEventManager.addAttributeListener(this)
    }

    override fun hide() {
        stage.clear()
    }

    override fun show() {
        game.serializer.loadGameState()
        stage.addActor(gameUI)
    }

    override fun render(delta: Float) {
        // teststuff
        when {
            Gdx.input.isKeyPressed(Input.Keys.NUM_1) -> game.mapManager.setMap(EMapType.MAP01)
            Gdx.input.isKeyPressed(Input.Keys.NUM_2) -> game.mapManager.setMap(EMapType.MAP02)
        }

        game.ecsEngine.update(delta)
        stage.viewport.apply(true)
        stage.act()
        stage.draw()
    }

    override fun pause() {
        game.serializer.saveGameState()
    }

    override fun resume() {

    }

    override fun resize(width: Int, height: Int) {
        game.gameViewPort.update(width, height, true)
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {

    }

    override fun inventoryResize(newSize: Int) {
        gameUI.inventoryUI.setInventorySize(newSize)
    }

    override fun inventorySlotUpdated(slotIdx: Int, item: Entity?) {
        if (item != null) {
            val stackCmp = game.cmpMapper.stackable.get(item)
            val equipType = game.cmpMapper.equipType.get(item)?.type
            if (stackCmp != null) {
                gameUI.inventoryUI.updateItemSlot(slotIdx, game.cmpMapper.render.get(item).texture, stackCmp.size, equipType)
            } else {
                gameUI.inventoryUI.updateItemSlot(slotIdx, game.cmpMapper.render.get(item).texture, equipType = equipType)
            }
            if (slotIdx == gameUI.inventoryUI.selectedSlotIdx) {
                // special case for stackable items
                // if item is shown in inventory UI and a new item is obtained then the stack counter
                // needs to be updated
                inputShowInventoryItem(slotIdx)
            }
        } else {
            gameUI.inventoryUI.updateItemSlot(slotIdx, "", equipType = null)
        }
    }

    override fun equipSlotUpdated(entity: Entity, type: EEquipType, prevItem: Entity?, newItem: Entity?) {
        if (newItem != null) {
            gameUI.inventoryUI.updateEquipSlot(type, game.cmpMapper.render.get(newItem).texture)
        } else {
            gameUI.inventoryUI.updateEquipSlot(type, "")
        }
    }

    private fun getItemNameString(item: Entity): String {
        val descCmp = game.cmpMapper.description.get(item)
        val priceCmp = game.cmpMapper.price.get(item)
        val stackCmp = game.cmpMapper.stackable.get(item)

        strBuffer.setLength(0)
        if (stackCmp != null) {
            strBuffer.append(stackCmp.size)
            strBuffer.append("x ")
        }
        strBuffer.append(descCmp.name)
        if (priceCmp != null) {
            strBuffer.append(" (")
            strBuffer.append(priceCmp.price)
            strBuffer.append(" G")
            strBuffer.append(")")
        }
        return strBuffer.toString()
    }

    override fun inputShowInventoryItem(slotIdx: Int) {
        val item = inventorySystem.getInventoryItem(idSystem.getPlayerEntity(), slotIdx)
        if (item != null) {
            val descCmp = game.cmpMapper.description.get(item)
            gameUI.inventoryUI.updateItemInfo(slotIdx, getItemNameString(item), descCmp.description, game.cmpMapper.render.get(item).texture)
        }
    }

    override fun inputShowEquipmentItem(type: EEquipType) {
        val item = equipSystem.getEquipmentItem(idSystem.getPlayerEntity(), type)
        if (item != null) {
            val descCmp = game.cmpMapper.description.get(item)
            gameUI.inventoryUI.updateItemInfo(-1, getItemNameString(item), descCmp.description, game.cmpMapper.render.get(item).texture)
        }
    }

    override fun inputShowInventory() {
        val player = idSystem.getPlayerEntity()
        val inventoryCmp = game.cmpMapper.inventory.get(player)
        for (index in 0 until inventoryCmp.items.size) {
            inventorySlotUpdated(index, inventorySystem.getInventoryItem(player, index))
        }
        for (type in EEquipType.values()) {
            if (type == EEquipType.UNDEFINED) continue
            equipSlotUpdated(idSystem.getPlayerEntity(), type, null, equipSystem.getEquipmentItem(player, type))
        }
    }

    override fun inputShowStats() {
        val player = idSystem.getPlayerEntity()
        val attributeCmp = game.cmpMapper.attribute.get(player)

        gameUI.statsUI.updateLife(attributeCmp.attributes[EAttributeType.LIFE.ordinal], attributeCmp.attributes[EAttributeType.MAX_LIFE.ordinal])
        gameUI.statsUI.updateMana(attributeCmp.attributes[EAttributeType.MANA.ordinal], attributeCmp.attributes[EAttributeType.MAX_MANA.ordinal])
        gameUI.statsUI.updateStrength(attributeCmp.attributes[EAttributeType.STRENGTH.ordinal])
        gameUI.statsUI.updateAgility(attributeCmp.attributes[EAttributeType.AGILITY.ordinal])
        gameUI.statsUI.updateIntelligence(attributeCmp.attributes[EAttributeType.INTELLIGENCE.ordinal])
    }

    override fun attributeUpdated(entity: Entity, type: EAttributeType, newValue: Float) {
        if (entity == idSystem.getPlayerEntity()) {
            when (type) {
                EAttributeType.LIFE, EAttributeType.MAX_LIFE -> {
                    val attributeCmp = game.cmpMapper.attribute.get(entity)
                    gameUI.statsUI.updateLife(attributeCmp.attributes[EAttributeType.LIFE.ordinal], attributeCmp.attributes[EAttributeType.MAX_LIFE.ordinal])
                }
                EAttributeType.MANA, EAttributeType.MAX_MANA -> {
                    val attributeCmp = game.cmpMapper.attribute.get(entity)
                    gameUI.statsUI.updateMana(attributeCmp.attributes[EAttributeType.MANA.ordinal], attributeCmp.attributes[EAttributeType.MAX_MANA.ordinal])
                }
                EAttributeType.STRENGTH -> {
                    gameUI.statsUI.updateStrength(newValue)
                }
                EAttributeType.INTELLIGENCE -> {
                    gameUI.statsUI.updateIntelligence(newValue)
                }
                EAttributeType.AGILITY -> {
                    gameUI.statsUI.updateAgility(newValue)
                }
            }
        }
    }
}