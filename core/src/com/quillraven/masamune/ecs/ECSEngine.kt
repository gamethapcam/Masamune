package com.quillraven.masamune.ecs

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.SerializationException
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.component.RemoveComponent
import com.quillraven.masamune.ecs.component.RenderComponent
import com.quillraven.masamune.ecs.system.*
import com.quillraven.masamune.serialization.CLASS_KEY

private const val TAG = "ECSEngine"

class ECSEngine constructor(private val game: MainGame) : PooledEngine(), Disposable {

    init {
        addSystem(PlayerInputSystem(game))
        addSystem(Box2DSystem(game, this))
        addSystem(CameraSystem(game)) // add AFTER box2d system to use the calculated interpolated values
        addSystem(RenderFlipSystem(game))
        addSystem(GameRenderSystem(game))
        addSystem(RemoveSystem())

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

    // cmpData is a json array of serialized component data
    fun createEntityFromConfig(cmpData: JsonValue, posX: Float = 0f, posY: Float = 0f) {
        val componentPackage = "${RenderComponent::class.java.`package`.name}."
        val entity = createEntity()

        var iterator: JsonValue? = cmpData
        while (iterator != null) {
            val cmpClass = try {
                Class.forName("$componentPackage${iterator.getString(CLASS_KEY)}")
            } catch (e: Exception) {
                Gdx.app.error(TAG, "There is no component class of type $componentPackage${iterator.getString(CLASS_KEY)}", e)
                iterator = iterator.next
                continue
            }
            val cmp = try {
                @Suppress("UNCHECKED_CAST")
                createComponent(cmpClass as Class<Component>)
            } catch (e: ClassCastException) {
                Gdx.app.error(TAG, "Cannot cast class to component class", e)
                iterator = iterator.next
                continue
            }
            try {
                game.json.readFields(cmp, iterator)
            } catch (e: SerializationException) {
                Gdx.app.error(TAG, "Cannot set fields of component $cmp", e)
                iterator = iterator.next
                continue
            }
            entity.add(cmp)
            iterator = iterator.next
        }

        // set special location for entity if specified
        val transformCmp = game.cmpMapper.transform.get(entity)
        if (transformCmp != null && (posX != 0f || posY != 0f)) {
            transformCmp.x = posX
            transformCmp.y = posY
        }

        // set sprite if needed
        val renderCmp = game.cmpMapper.render.get(entity)
        if (renderCmp != null && !renderCmp.texture.isBlank()) {
            renderCmp.sprite = game.spriteCache.getSprite(renderCmp.texture)
        }

        // initialize width and height of transform component with default values if needed
        if (renderCmp != null && transformCmp != null && transformCmp.width == 0f && transformCmp.height == 0f) {
            transformCmp.width = renderCmp.width * 0.75f
            transformCmp.height = renderCmp.height * 0.2f
        }

        // create box2d body if needed
        val b2dCmp = game.cmpMapper.box2D.get(entity)
        if (b2dCmp != null && transformCmp != null) {
            val polygonShape = PolygonShape()
            polygonShape.setAsBox(transformCmp.width * 0.5f, transformCmp.height * 0.5f)
            b2dCmp.body = game.b2dUtils.createBody(
                    BodyDef.BodyType.values()[b2dCmp.type],
                    transformCmp.x + transformCmp.width * 0.5f,
                    transformCmp.y + transformCmp.height * 0.5f,
                    polygonShape,
                    entity
            )
        }

        addEntity(entity)
    }

    fun destroyCharacterEntities() {
        for (entity in entities) {
            if (game.cmpMapper.character.get(entity) != null) {
                entity.add(createComponent(RemoveComponent::class.java))
            }
        }
    }
}
