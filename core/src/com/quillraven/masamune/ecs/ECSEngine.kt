package com.quillraven.masamune.ecs

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.components.Box2DComponent
import com.quillraven.masamune.ecs.systems.Box2DSystem
import com.quillraven.masamune.ecs.systems.GameRenderSystem

class ECSEngine : PooledEngine() {
    private val game = Gdx.app.applicationListener as MainGame
    internal val box2DMapper = ComponentMapper.getFor(Box2DComponent::class.java)

    init {
        addSystem(Box2DSystem(game))
        addSystem(GameRenderSystem(game))
    }

}