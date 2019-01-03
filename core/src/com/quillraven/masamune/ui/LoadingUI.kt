package com.quillraven.masamune.ui

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton


class LoadingUI constructor(skin: Skin) : Table(skin) {
    private val bar = Bar(skin, "bar_green")

    init {
        setFillParent(true)

        bar.scaleY = 0.5f

        add(TextButton("Loading...", skin, "label")).fill().expand().bottom().row()
        add(bar).expand().fill().pad(0f, 25f, 10f, 25f).bottom()
    }

    fun setProgress(value: Float) {
        bar.progress = value
    }
}