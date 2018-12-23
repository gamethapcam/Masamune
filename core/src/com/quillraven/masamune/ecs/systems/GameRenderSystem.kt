package com.quillraven.masamune.ecs.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.quillraven.masamune.MainGame

class GameRenderSystem constructor(game: MainGame) : EntitySystem() {
    private val b2dDebugRenderer: Box2DDebugRenderer = Box2DDebugRenderer()
    private val viewport = game.gameViewPort
    private val batch = game.batch
    private val world = game.world

    override fun update(deltaTime: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        viewport.apply()
        b2dDebugRenderer.render(world, viewport.camera.combined)
    }
}