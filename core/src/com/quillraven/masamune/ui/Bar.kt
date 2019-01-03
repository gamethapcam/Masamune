package com.quillraven.masamune.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Widget

class Bar(skin: Skin, barGraphic: String) : Widget() {
    private val bgd = skin.getDrawable("bar")
    private val fgd = skin.getDrawable(barGraphic)
    internal var progress = 0f

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)

        bgd.draw(batch, x, y, 462f * scaleX, 83f * scaleY)
        fgd.draw(batch, x + 15f * scaleX, y + 22f * scaleY, MathUtils.clamp(progress * 462f, 0f, 462f - 2 * 15f) * scaleX, (83f - 2 * 21f) * scaleY)
    }
}