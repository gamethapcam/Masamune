package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.utils.Disposable
import com.quillraven.masamune.MainGame

class Box2DDebugRenderSystem constructor(game: MainGame) : EntitySystem(), Disposable {
    private val viewport = game.gameViewPort
    private val world = game.world
    private val b2dDebugRenderer = Box2DDebugRenderer()

    override fun update(deltaTime: Float) {
        b2dDebugRenderer.render(world, viewport.camera.combined)
    }

    override fun dispose() {
        b2dDebugRenderer.dispose()
    }
}