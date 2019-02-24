package com.quillraven.masamune.ui

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.quillraven.masamune.MainGame

abstract class DialogLight constructor(game: MainGame, titleKey: String) : Table(game.skin) {
    protected val contentTable = Table(skin)
    protected val btnClose = ImageButton(skin.getDrawable("btn_close"))

    init {
        this.setFillParent(true)

        // add title area and content to table
        // title area
        val label = TextButton("[DIALOG_TITLE_LIGHT]${game.resourceBundle.get(titleKey)}", skin, "dialog_title")
        val imgSkull = Image(skin.getDrawable("skull"))
        imgSkull.setScale(0.75f, 0.75f)
        this.add(imgSkull).padBottom(-25f).padLeft(70f).colspan(2).row()
        this.add(label).height(130f).right().padLeft(90f)
        this.add(btnClose).left().padLeft(-5f).row()
        imgSkull.toFront()

        // content
        contentTable.background = skin.getDrawable("dialog_light")
        contentTable.pad(40f, 40f, 35f, 0f)
        this.add(contentTable).padBottom(30f).padTop(-5f).colspan(2)

        // move little bit to the right to center between touchpad and action button
        this.padLeft(60f)

        // close button hides the dialog
        btnClose.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                // clear item info and hide inventory UI
                beforeClose()
                stage.root.removeActor(this@DialogLight)
                return true
            }
        })
    }

    protected open fun beforeClose() {}
}