package com.quillraven.masamune.ui

import com.badlogic.gdx.scenes.scene2d.ui.*


class LoadingUI constructor(skin: Skin) : Table(skin) {
    private val bar = Bar(0f, 1f, 0.01f, skin, "green")

    init {
        setFillParent(true)

        // important to set width, height and fill otherwise the draw method of ProgressBar will not work correctly
        val x = Stack()
        x.add(bar)
        x.add(TextButton("Loading...", skin, "label"))
        add(x).width(Value.percentWidth(0.9f, this)).height(100f).fill().bottom().padBottom(30f)

        bottom()
    }

    fun setProgress(value: Float) {
        bar.value = value
    }
}