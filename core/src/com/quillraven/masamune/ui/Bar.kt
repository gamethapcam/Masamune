package com.quillraven.masamune.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.ui.Skin

class Bar constructor(min: Float, max: Float, stepSize: Float, skin: Skin, styleName: String) : ProgressBar(min, max, stepSize, false, skin, styleName) {
    private var knobPos = 0f

    override fun getKnobPosition(): Float {
        return knobPos
    }

    // fixed draw method of progressbar to use correct width and height of the cell
    override fun draw(batch: Batch, parentAlpha: Float) {
        val style = this.style
        val disabled = this.isDisabled
        val bg = if (disabled && style.disabledBackground != null) style.disabledBackground else style.background
        val knobBefore = if (disabled && style.disabledKnobBefore != null) style.disabledKnobBefore else style.knobBefore

        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha)

        var positionWidth = width

        var bgLeftWidth = 0f
        var bgBottomHeight = 0f
        var bgTopHeight = 0f
        if (bg != null) {
            bg.draw(batch, x, y, width, height)
            bgLeftWidth = bg.leftWidth
            positionWidth -= bgLeftWidth + bg.rightWidth
            bgBottomHeight = bg.bottomHeight
            bgTopHeight = bg.topHeight
        }

        knobPos = positionWidth * visualPercent
        knobPos = Math.min(positionWidth, knobPos)
        knobPos = Math.max(0f, knobPos)
        knobBefore?.draw(batch, x + bgLeftWidth, y + bgBottomHeight, knobPos, Math.max(0f, height - bgTopHeight - bgBottomHeight))
    }
}