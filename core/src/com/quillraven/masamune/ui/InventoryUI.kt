package com.quillraven.masamune.ui

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.quillraven.masamune.event.GameEventManager

class InventoryUI constructor(skin: Skin, private val eventMgr: GameEventManager) : Table(skin) {
    private val content = Table(skin)

    init {
        setFillParent(true)

        // title area
        val label = TextButton("Inventory", skin, "dialog_title")
        val imgSkull = Image(skin.getDrawable("skull"))
        val btnClose = ImageButton(skin.getDrawable("btn_close"))
        imgSkull.setScale(0.75f, 0.75f)
        add(imgSkull).padBottom(-25f).padLeft(70f).colspan(2).row()
        add(label).size(Value.percentWidth(0.6f, this), Value.prefHeight).height(130f).right().padLeft(90f)
        add(btnClose).left().padLeft(-5f).row()
        imgSkull.toFront()

        // content
        content.background = skin.getDrawable("dialog_light")
        add(content).colspan(2).padBottom(30f).padTop(-5f)

        // move little bit to the right to center between touchpad and action button
        padLeft(60f)

        // listeners
        btnClose.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                stage.root.removeActor(this@InventoryUI)
                return true
            }
        })
    }
}