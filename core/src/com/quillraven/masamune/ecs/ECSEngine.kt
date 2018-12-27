package com.quillraven.masamune.ecs

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ObjectMap
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.bodyDef
import com.quillraven.masamune.ecs.component.*
import com.quillraven.masamune.ecs.system.*
import com.quillraven.masamune.fixtureDef
import com.quillraven.masamune.model.CharacterCfg
import com.quillraven.masamune.model.ECharactedType
import com.quillraven.masamune.resetBodyAndFixtureDef

internal val CmpMapperB2D = ComponentMapper.getFor(Box2DComponent::class.java)
internal val CmpMapperRender = ComponentMapper.getFor(RenderComponent::class.java)
internal val CmpMapperFlip = ComponentMapper.getFor(RenderFlipComponent::class.java)
internal val CmpMapperMove = ComponentMapper.getFor(MoveComponent::class.java)

private const val TAG = "ECSEngine"

class ECSEngine : PooledEngine(), Disposable {
    private val game = Gdx.app.applicationListener as MainGame
    private val texAtlas = game.assetManager.get("textures.atlas", TextureAtlas::class.java)
    private val spriteCache = ObjectMap<String, Sprite>()

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

    fun createCharacter(x: Float, y: Float, cfg: CharacterCfg) {
        val entity = createEntity()

        resetBodyAndFixtureDef()
        entity.add(createComponent(Box2DComponent::class.java).apply {
            width = cfg.width * 0.75f
            height = cfg.height * 0.2f
            prevX = x + width * 0.5f
            prevY = y + height * 0.5f
            interpolatedX = prevX
            interpolatedY = prevY

            bodyDef.type = cfg.bodyType
            bodyDef.position.set(prevX, prevY)
            bodyDef.fixedRotation = true
            body = game.world.createBody(bodyDef).apply {
                val polygonShape = PolygonShape()
                polygonShape.setAsBox(width * 0.5f, height * 0.5f)
                fixtureDef.shape = polygonShape
                fixtureDef.isSensor = false
                createFixture(fixtureDef)
                polygonShape.dispose()
            }
        })

        if (cfg.flip) {
            entity.add(createComponent(RenderFlipComponent::class.java))
        }
        entity.add(createComponent(RenderComponent::class.java).apply {
            sprite = getSprite(cfg.texture)
            texturePath.append(cfg.texture)
            width = cfg.width
            height = cfg.height
        })
        entity.add(createComponent(MoveComponent::class.java).apply { speed = cfg.speed })

        if (cfg.type == ECharactedType.HERO) {
            // player entity -> add player input and camera lock components
            entity.add(createComponent(CameraComponent::class.java))
            entity.add(createComponent(PlayerInputComponent::class.java))
        }

        addEntity(entity)
    }

    private fun getSprite(texture: String): Sprite {
        var sprite = spriteCache.get(texture)
        if (sprite == null) {
            Gdx.app.debug(TAG, "Creating sprite $texture")
            sprite = texAtlas.createSprite(texture)
            if (sprite == null) {
                Gdx.app.error(TAG, "Could not find texture region $texture. Using default sprite instead")
                return getDefaultSprite()
            }
            spriteCache.put(texture, sprite)
        }
        return sprite
    }

    private fun getDefaultSprite(): Sprite {
        var defaultSprite = spriteCache.get("frederick_new")
        if (defaultSprite == null) {
            Gdx.app.debug(TAG, "Creating default sprite")
            defaultSprite = texAtlas.createSprite("frederick_new")
            spriteCache.put("frederick_new", defaultSprite)
        }
        return defaultSprite
    }
}