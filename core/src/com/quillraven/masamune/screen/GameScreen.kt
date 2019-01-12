package com.quillraven.masamune.screen

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.quillraven.masamune.ecs.system.DEFAULT_ENTITY_ID
import com.quillraven.masamune.ecs.system.IdentifySystem
import com.quillraven.masamune.ecs.system.InventorySystem
import com.quillraven.masamune.event.InputListener
import com.quillraven.masamune.event.ItemListener
import com.quillraven.masamune.map.EMapType
import com.quillraven.masamune.ui.GameUI

class GameScreen : Q2DScreen(), InputListener, ItemListener {
    private val gameUI = GameUI(game.skin, game.gameEventManager)

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
        val inventoryCmp = game.ecsEngine.getSystem(InventorySystem::class.java).getInventory(game.ecsEngine.getSystem(IdentifySystem::class.java).getPlayerEntity()!!)
        gameUI.inventoryUI.setInventorySize(inventoryCmp?.maxSize ?: 0)
    }

    override fun itemMoved(fromSlotIdx: Int, toSlotIdx: Int) {
        game.ecsEngine.getSystem(InventorySystem::class.java).moveItem(game.ecsEngine.getSystem(IdentifySystem::class.java).getPlayerEntity()!!, fromSlotIdx, toSlotIdx)
    }

    override fun itemSlotUpdated(slotIdx: Int, item: Entity?) {
        resizeInventoryUI()
        gameUI.inventoryUI.updateItemSlot(slotIdx, if (item != null) game.cmpMapper.render.get(item).texture else "")
    }

    override fun inputShowItem(slotIdx: Int) {
        val item = game.ecsEngine.getSystem(InventorySystem::class.java).getInventoryItem(game.ecsEngine.getSystem(IdentifySystem::class.java).getPlayerEntity()!!, slotIdx)
        if (item != null) {
            gameUI.inventoryUI.updateItemInfo("X", "Y", game.cmpMapper.render.get(item).texture)
        }
    }

    override fun inputShowInventory() {
        val inventoryCmp = game.ecsEngine.getSystem(InventorySystem::class.java).getInventory(game.ecsEngine.getSystem(IdentifySystem::class.java).getPlayerEntity()!!)
        if (inventoryCmp != null) {
            resizeInventoryUI()
            for (index in 0 until inventoryCmp.items.size) {
                if (inventoryCmp.items[index] == DEFAULT_ENTITY_ID) continue

                gameUI.inventoryUI.updateItemSlot(index, game.cmpMapper.render.get(game.ecsEngine.getSystem(IdentifySystem::class.java).getEntityByID(inventoryCmp.items[index])).texture)
            }
        }
    }
}