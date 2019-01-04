package com.quillraven.masamune.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.quillraven.masamune.event.GameEventManager


class GameUI constructor(skin: Skin, private val eventMgr: GameEventManager) : Table(skin) {
    init {
        setFillParent(true)

        val touchpad = Touchpad(0f, skin)
        touchpad.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                eventMgr.dispatchInputMoveEvent(touchpad.knobPercentX, touchpad.knobPercentY)
            }
        })
        add(touchpad).bottom().left().pad(0f, 30f, 30f, 0f).size(Value.percentWidth(0.15f,this))

        val btn = TextButton("A", skin, "action")
        btn.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                eventMgr.dispatchInputActionEvent()
                return true
            }
        })
        add(btn).bottom().expandX().right().pad(0f, 0f, 30f, 30f).size(Value.percentWidth(0.1f,this))

        bottom()
    }
}