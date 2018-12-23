package com.quillraven.masamune

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2D
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.FitViewport
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.screen.GameScreen

private const val TAG = "Main"

class MainGame : Q2DGame() {
    internal val gameViewPort by lazy { FitViewport(16f, 9f) }
    internal val batch by lazy { SpriteBatch() }
    internal val ecsEngine by lazy { ECSEngine() }
    internal val world by lazy { World(Vector2(0f, 0f), true) }

    override fun create() {
        Box2D.init()
        setScreen(GameScreen::class.java)
    }

    override fun dispose() {
        Gdx.app.debug(TAG, "Maximum sprites in batch: ${batch.maxSpritesInBatch}. Current batch size is 1000. Increase it if max is higher.")

        batch.dispose()
        world.dispose()
        super.dispose()
    }
}
