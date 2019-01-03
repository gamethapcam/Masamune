package com.quillraven.masamune.ecs

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.SerializationException
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.component.CharacterComponent
import com.quillraven.masamune.ecs.component.RemoveComponent
import com.quillraven.masamune.ecs.system.*
import com.quillraven.masamune.model.ECharacterType
import com.quillraven.masamune.serialization.CLASS_KEY

private const val TAG = "ECSEngine"

class ECSEngine constructor(private val game: MainGame) : PooledEngine(), Disposable {
    private val characterEntities = getEntitiesFor(Family.all(CharacterComponent::class.java).exclude(RemoveComponent::class.java).get())

    init {
        addSystem(PlayerInputSystem(game))
        addSystem(Box2DSystem(game, this))
        addSystem(CameraSystem(game)) // add AFTER box2d system to use the calculated interpolated values
        addSystem(ActionableSystem(game, this))
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

    private fun getComponentClassByName(cmpName: String): Class<Component>? {
        val cmpClass = try {
            Class.forName(cmpName)
        } catch (e: Exception) {
            Gdx.app.error(TAG, "There is no component class of type $cmpName", e)
            return null
        }
        try {
            @Suppress("UNCHECKED_CAST")
            return cmpClass as Class<Component>
        } catch (e: ClassCastException) {
            Gdx.app.error(TAG, "Cannot cast class to component class", e)
            return null
        }
    }

    private fun setEntitySprite(entity: Entity) {
        val renderCmp = game.cmpMapper.render.get(entity)
        if (renderCmp != null && !renderCmp.texture.isBlank()) {
            renderCmp.sprite = game.spriteCache.getSprite(renderCmp.texture)
        }
    }

    // cmpData is a json array of serialized component data
    fun createEntityFromConfig(cmpData: JsonValue, posX: Float = 0f, posY: Float = 0f, widthScale: Float = 1f, heightScale: Float = 1f) {
        val entity = createEntity()

        var iterator: JsonValue? = cmpData
        while (iterator != null) {
            val value = iterator
            iterator = iterator.next

            val cmpClass = getComponentClassByName(value.getString(CLASS_KEY)) ?: continue
            val cmp = createComponent(cmpClass)
            entity.add(cmp)
            try {
                game.json.readFields(cmp, value)
            } catch (e: SerializationException) {
                Gdx.app.error(TAG, "Cannot set fields of component $cmp", e)
            }
        }

        // set special location for entity if specified
        val transformCmp = game.cmpMapper.transform.get(entity)
        if (transformCmp != null && (posX != 0f || posY != 0f)) {
            transformCmp.x = posX
            transformCmp.y = posY
        }

        // set sprite if needed
        val renderCmp = game.cmpMapper.render.get(entity)
        setEntitySprite(entity)

        // initialize width and height of transform component with default values if needed
        if (renderCmp != null && transformCmp != null && transformCmp.width == 0f && transformCmp.height == 0f) {
            transformCmp.width = renderCmp.width * widthScale
            transformCmp.height = renderCmp.height * heightScale
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
                    entity,
                    b2dCmp.detectionRadius
            )
        }

        addEntity(entity)
    }

    fun getCharacterEntityByCharacterType(charType: ECharacterType): Entity? {
        for (e in characterEntities) {
            val charCmp = game.cmpMapper.character.get(e)
            if (charCmp.type == charType) {
                return e
            }
        }
        return null
    }

    // helper method to set the component values of an entity to the given cmpData
    // this is used f.e. when changing from one map to another to take over the player entity configuration
    // from the old map to the new map
    // cmpData is a json array of serialized component data
    fun initCharacterEntityFromConfig(charType: ECharacterType, cmpData: JsonValue) {
        val entity = getCharacterEntityByCharacterType(charType)
        if (entity == null) {
            Gdx.app.debug(TAG, "There is no entity of type $charType that needs to be initialized")
            return
        }

        var iterator: JsonValue? = cmpData
        while (iterator != null) {
            val value = iterator
            iterator = iterator.next

            val cmpClass = getComponentClassByName(value.getString(CLASS_KEY)) ?: continue
            var cmp = entity.getComponent(cmpClass)
            if (cmp == null) {
                cmp = createComponent(cmpClass)
                entity.add(cmp)
            }
            try {
                game.json.readFields(cmp, value)
            } catch (e: SerializationException) {
                Gdx.app.error(TAG, "Cannot set fields of component $cmp", e)
            }
        }

        setEntitySprite(entity)
    }

    fun destroyCharacterEntities() {
        for (entity in entities) {
            if (game.cmpMapper.character.get(entity) != null) {
                entity.add(createComponent(RemoveComponent::class.java))
            }
        }
    }

    fun destroyObjectEntities() {
        for (entity in entities) {
            if (game.cmpMapper.obj.get(entity) != null) {
                entity.add(createComponent(RemoveComponent::class.java))
            }
        }
    }
}
