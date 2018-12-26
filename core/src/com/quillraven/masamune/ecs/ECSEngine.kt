package com.quillraven.masamune.ecs

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.utils.Disposable
import com.quillraven.masamune.*
import com.quillraven.masamune.ecs.component.*
import com.quillraven.masamune.ecs.system.*

internal val CmpMapperB2D = ComponentMapper.getFor(Box2DComponent::class.java)
internal val CmpMapperRender = ComponentMapper.getFor(RenderComponent::class.java)
internal val CmpMapperFlip = ComponentMapper.getFor(RenderFlipComponent::class.java)
internal val CmpMapperMove = ComponentMapper.getFor(MoveComponent::class.java)

class ECSEngine : PooledEngine(), Disposable {
    private val game = Gdx.app.applicationListener as MainGame

    init {
        addSystem(PlayerInputSystem(game))
        addSystem(Box2DSystem(game))
        addSystem(CameraSystem(game))
        addSystem(RenderFlipSystem())
        addSystem(GameRenderSystem(game))

        // debug stuff
        // addSystem(Box2DDebugRenderSystem(game))
    }

    override fun dispose() {
        for (system in systems) {
            if (system is Disposable) {
                system.dispose()
            }
        }
    }

    fun createCharacter(x: Float, y: Float, cfg: CharacterCfg): Entity {
        val entity = createEntity()

        resetBodyAndFixtureDef()
        entity.add(createComponent(Box2DComponent::class.java).apply {
            width = cfg.width * 0.75f
            height = cfg.height * 0.2f
            prevX = x
            prevY = y
            interpolatedX = x
            interpolatedY = y

            bodyDef.type = cfg.type
            bodyDef.position.set(x + width * 0.5f, y + height * 0.5f)
            bodyDef.fixedRotation = true
            body = game.world.createBody(bodyDef)

            val polygonShape = PolygonShape()
            polygonShape.setAsBox(width * 0.5f, height * 0.5f)
            fixtureDef.shape = polygonShape
            fixtureDef.isSensor = false
            body!!.createFixture(fixtureDef)
            polygonShape.dispose()
        })

        if (cfg.flip) {
            entity.add(createComponent(RenderFlipComponent::class.java))
        }
        entity.add(createComponent(RenderComponent::class.java).apply {
            texturePath.append(cfg.texture)
            width = cfg.width
            height = cfg.height
        })
        entity.add(createComponent(MoveComponent::class.java).apply { speed = cfg.speed })

        addEntity(entity)
        return entity
    }

    fun createPlayer(x: Float, y: Float) {
        createCharacter(x, y, game.characterCfgMap.get("player")).apply {
            add(createComponent(CameraComponent::class.java))
            add(createComponent(PlayerInputComponent::class.java))
        }
    }
}