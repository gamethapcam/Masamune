package com.quillraven.masamune.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Widget

class Bar(skin: Skin, barGraphic: String) : Widget() {
    private val bgd = skin.getDrawable("bar")
    private val fgd = skin.getDrawable(barGraphic)
    internal var progress = 0f

    // don't forget to call fill and expand to have a width and height value otherwise those values are zero and the rendering is wrong
    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)

        bgd.draw(batch, x, y, width * scaleX, height * scaleY)
        fgd.draw(batch, x + 15f * scaleX, y + 33f * scaleY,
                MathUtils.clamp(progress * width, 0f, width - 2 * 15f) * scaleX,
                Math.max(0f, (height - 2 * 32f) * scaleY))
    }
}