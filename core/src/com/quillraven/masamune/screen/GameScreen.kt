package com.quillraven.masamune.screen

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.utils.StringBuilder
import com.quillraven.masamune.ecs.system.DEFAULT_ENTITY_ID
import com.quillraven.masamune.ecs.system.IdentifySystem
import com.quillraven.masamune.ecs.system.InventorySystem
import com.quillraven.masamune.event.InputListener
import com.quillraven.masamune.event.ItemListener
import com.quillraven.masamune.map.EMapType
import com.quillraven.masamune.ui.GameUI

class GameScreen : Q2DScreen(), InputListener, ItemListener {
    private val gameUI = GameUI(game)
    private val strBuffer = StringBuilder(0)

    init {
        game.gameEventManager.addInputListener(this)
        game.gameEventManager.addItemListener(this)
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

    private fun resizeInventoryUI() {
        val inventoryCmp = game.ecsEngine.getSystem(InventorySystem::class.java).getInventory(game.ecsEngine.getSystem(IdentifySystem::class.java).getPlayerEntity())
        gameUI.inventoryUI.setInventorySize(inventoryCmp?.maxSize ?: 0)
    }

    override fun itemMoved(fromSlotIdx: Int, toSlotIdx: Int) {
        game.ecsEngine.getSystem(InventorySystem::class.java).moveItem(game.ecsEngine.getSystem(IdentifySystem::class.java).getPlayerEntity(), fromSlotIdx, toSlotIdx)
    }

    override fun itemSlotUpdated(slotIdx: Int, item: Entity?) {
        resizeInventoryUI()
        if (item != null) {
            val stackCmp = game.cmpMapper.stackable.get(item)
            if (stackCmp != null) {
                gameUI.inventoryUI.updateItemSlot(slotIdx, game.cmpMapper.render.get(item).texture, stackCmp.size)
            } else {
                gameUI.inventoryUI.updateItemSlot(slotIdx, game.cmpMapper.render.get(item).texture)
            }
        } else {
            gameUI.inventoryUI.updateItemSlot(slotIdx, "")
        }
    }

    override fun inputShowItem(slotIdx: Int) {
        val item = game.ecsEngine.getSystem(InventorySystem::class.java).getInventoryItem(game.ecsEngine.getSystem(IdentifySystem::class.java).getPlayerEntity(), slotIdx)
        if (item != null) {
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

            gameUI.inventoryUI.updateItemInfo(strBuffer.toString(), descCmp.description, game.cmpMapper.render.get(item).texture)
        }
    }

    override fun inputShowInventory() {
        val inventoryCmp = game.ecsEngine.getSystem(InventorySystem::class.java).getInventory(game.ecsEngine.getSystem(IdentifySystem::class.java).getPlayerEntity())
        if (inventoryCmp != null) {
            resizeInventoryUI()
            for (index in 0 until inventoryCmp.items.size) {
                if (inventoryCmp.items[index] == DEFAULT_ENTITY_ID) continue

                itemSlotUpdated(index, game.ecsEngine.getSystem(IdentifySystem::class.java).getEntityByID(inventoryCmp.items[index]))
            }
        }
    }
}