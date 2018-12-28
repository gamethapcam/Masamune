package com.quillraven.masamune.ecs

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.utils.Disposable
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.component.*
import com.quillraven.masamune.ecs.system.*
import com.quillraven.masamune.model.CharacterCfg
import com.quillraven.masamune.model.ECharacterType

class ECSEngine : PooledEngine(), Disposable {
    private val game = Gdx.app.applicationListener as MainGame

    init {
        addSystem(PlayerInputSystem(game))
        addSystem(Box2DSystem(game))
        addSystem(CameraSystem(game)) // add AFTER box2d system to use the calculated interpolated values
        addSystem(RenderFlipSystem(game))
        addSystem(GameRenderSystem(game))

        // debug stuff
        addSystem(Box2DDebugRenderSystem(game))
    }

    override fun dispose() {
        for (system in systems) {
            if (system is Disposable) {
                system.dispose()
            }
        }
    }

    fun createCharacter(posX: Float, posY: Float, cfg: CharacterCfg) {
        val entity = createEntity()

        val w = cfg.width * 0.75f
        val h = cfg.height * 0.2f
        entity.add(createComponent(TransformComponent::class.java).apply {
            width = w
            height = h
            x = posX
            y = posY
            prevX = x
            prevY = y
            interpolatedX = prevX
            interpolatedY = prevY
        })

        entity.add(createComponent(Box2DComponent::class.java).apply {
            val polygonShape = PolygonShape()
            polygonShape.setAsBox(w * 0.5f, h * 0.5f)
            body = game.b2dUtils.createBody(cfg.bodyType, posX + w * 0.5f, posY + h * 0.5f, polygonShape)
        })

        if (cfg.flip) {
            entity.add(createComponent(RenderFlipComponent::class.java))
        }

        entity.add(createComponent(RenderComponent::class.java).apply {
            sprite = game.spriteCache.getSprite(cfg.texture)
            texturePath = cfg.texture
            width = cfg.width
            height = cfg.height
        })

        entity.add(createComponent(MoveComponent::class.java).apply { speed = cfg.speed })

        if (cfg.type == ECharacterType.HERO) {
            // player entity -> add player input and camera lock components
            entity.add(createComponent(CameraComponent::class.java))
            entity.add(createComponent(PlayerInputComponent::class.java))
        }

        addEntity(entity)
    }
}
