package com.quillraven.masamune.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.quillraven.masamune.MainGame

private const val TAG = "ConversationUI"

class ConversationUI constructor(game: MainGame) : DialogLight(game, "Dialog") {
    private val resourceBundle = game.resourceBundle
    private val eventMgr = game.gameEventManager

    private val img = Image()
    private val text = TextButton("", skin, "label")
    private val maxOptions = 3
    private val options = Array<TextButton>(maxOptions)

    init {
        btnClose.isVisible = false

        contentTable.add(img).expand().size(128f, 128f)
        text.label.setAlignment(Align.left)
        contentTable.add(text).fill().expand().colspan(maxOptions - 1).row()

        for (i in maxOptions - 1 downTo 0) {
            val option = TextButton("", skin, "conversation_option")
            option.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    eventMgr.dispatchInputConversationLink(i)
                }
            })

            options.add(option)
            contentTable.add(option).fill().padLeft(50f).padRight(50f).padBottom(50f)
        }
    }

    fun updateConversation(imgKey: String, txtKey: String) {
        img.drawable = skin.getDrawable(resourceBundle.get(imgKey))
        text.setText("[BLACK]${resourceBundle.get(txtKey)}")

        for (i in 0 until maxOptions) {
            options[i].setText("")
            options[i].touchable = Touchable.disabled
            options[i].isVisible = false
        }
    }

    fun addOption(optKey: String) {
        for (i in maxOptions - 1 downTo 0) {
            if (options[i].touchable == Touchable.disabled) {
                options[i].touchable = Touchable.enabled
                options[i].isVisible = true
                options[i].setText("[HIGHLIGHT]${resourceBundle.get(optKey)}")
                return
            }
        }

        Gdx.app.error(TAG, "Current conversation has too many options")
    }
}