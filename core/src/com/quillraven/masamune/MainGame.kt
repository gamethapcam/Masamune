package com.quillraven.masamune

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.quillraven.masamune.screen.GameScreen

private const val TAG = "Main"
class MainGame : Q2DGame() {
    internal val batch by lazy { SpriteBatch() }

    override fun create() {
        setScreen(GameScreen::class.java)
    }

    override fun dispose() {
        Gdx.app.debug(TAG, "Maximum sprites in batch: ${batch.maxSpritesInBatch}. Current batch size is 1000. Increase it if max is higher.")

        batch.dispose()
        super.dispose()
    }
}
