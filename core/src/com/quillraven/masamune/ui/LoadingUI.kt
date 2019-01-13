package com.quillraven.masamune.ui

import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Value
import com.quillraven.masamune.MainGame


class LoadingUI constructor(game: MainGame) : Table(game.skin) {
    private val bar = Bar(0f, 1f, 0.01f, game.skin, "green")

    init {
        setFillParent(true)

        // important to set width, height and fill otherwise the draw method of ProgressBar will not work correctly
        val x = Stack()
        x.add(bar)
        x.add(TextButton("${game.resourceBundle.get("Loading")}...", skin, "label"))
        add(x).width(Value.percentWidth(0.9f, this)).height(100f).fill().bottom().padBottom(30f)

        bottom()
    }

    fun setProgress(value: Float) {
        bar.value = value
    }
}