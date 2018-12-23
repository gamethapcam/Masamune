package com.quillraven.masamune.ecs

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Disposable
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.component.Box2DComponent
import com.quillraven.masamune.ecs.system.Box2DSystem
import com.quillraven.masamune.ecs.system.GameRenderSystem

class ECSEngine : PooledEngine(), Disposable {
    private val game = Gdx.app.applicationListener as MainGame
    internal val box2DMapper = ComponentMapper.getFor(Box2DComponent::class.java)

    init {
        addSystem(Box2DSystem(game))
        addSystem(GameRenderSystem(game))
    }

    override fun dispose() {
        for (system in systems) {
            if (system is Disposable) {
                system.dispose()
            }
        }
    }
}