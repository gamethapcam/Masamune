package com.quillraven.masamune.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.quillraven.masamune.MainGame


class GameUI constructor(game: MainGame) : Table(game.skin) {
    private val eventMgr = game.gameEventManager
    internal val inventoryUI = InventoryUI(game)
    internal val statsUI = StatsUI(game)
    internal val conversationUI = ConversationUI(game)

    init {
        setFillParent(true)

        val touchpad = Touchpad(0f, skin)
        touchpad.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                eventMgr.dispatchInputMoveEvent(touchpad.knobPercentX, touchpad.knobPercentY)
            }
        })
        add(touchpad).bottom().left().pad(0f, 30f, 30f, 0f).size(Value.percentWidth(0.15f, this)).expandX()

        // inventory
        val btnInventory = TextButton("", skin, "action")
        val imgInventory = Image(skin.getDrawable("inventory"))
        imgInventory.touchable = Touchable.disabled
        imgInventory.setPosition(18f, 19f)
        btnInventory.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (inventoryUI.stage == null) {
                    eventMgr.dispatchInputShowInventory()
                    stage.addActor(inventoryUI)
                }
                return true
            }
        })

        // stats + skills
        val btnSkills = TextButton("", skin, "action")
        val imgSkills = Image(skin.getDrawable("stats"))
        imgSkills.touchable = Touchable.disabled
        imgSkills.setPosition(14f, 17f)
        btnSkills.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (statsUI.stage == null) {
                    eventMgr.dispatchInputShowStats()
                    stage.addActor(statsUI)
                }
                return true
            }
        })
        val skillGroup = WidgetGroup(btnSkills, imgSkills)
        skillGroup.setPosition(35f, 55f)
        val inventoryGroup = WidgetGroup(btnInventory, imgInventory)
        inventoryGroup.setPosition(-10f, -10f)
        add(WidgetGroup(skillGroup, inventoryGroup)).right().size(Value.percentWidth(0.1f, this)).padBottom(-15f).padRight(-25f)

        // action
        val btnAction = TextButton("A", skin, "action")
        btnAction.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                eventMgr.dispatchInputActionEvent()
                return true
            }
        })
        add(btnAction).bottom().right().pad(0f, 0f, 30f, 30f).size(Value.percentWidth(0.1f, this))

        bottom()
    }

    fun toggleConversationUI(show: Boolean) {
        if (show && conversationUI.stage == null) {
            stage.addActor(conversationUI)
        } else if (!show && conversationUI.stage != null) {
            stage.root.removeActor(conversationUI)
        }
    }
}