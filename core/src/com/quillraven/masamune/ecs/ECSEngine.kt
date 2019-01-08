package com.quillraven.masamune.ecs

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.SerializationException
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.component.RemoveComponent
import com.quillraven.masamune.ecs.system.*
import com.quillraven.masamune.serialization.CLASS_KEY

private const val TAG = "ECSEngine"

class ECSEngine constructor(private val game: MainGame) : PooledEngine(), Disposable {

    init {
        addSystem(IdentifySystem(game, this))
        addSystem(PlayerInputSystem(game))
        addSystem(Box2DSystem(game, this))
        addSystem(CameraSystem(game)) // add AFTER box2d system to use the calculated interpolated values
        addSystem(ActionableSystem(game, this))
        addSystem(InventorySystem(game, this))
        addSystem(RenderFlipSystem(game))
        addSystem(GameRenderSystem(game))
        addSystem(RemoveSystem())

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

        // initialize width and height of transform component with default values if needed
        val renderCmp = game.cmpMapper.render.get(entity)
        if (renderCmp != null && transformCmp != null && transformCmp.width == 0f && transformCmp.height == 0f) {
            transformCmp.width = renderCmp.width * widthScale
            transformCmp.height = renderCmp.height * heightScale
        }

        addEntity(entity)
    }

    fun destroyEntitiesOfType(type: EntityType) {
        for (entity in getSystem(IdentifySystem::class.java).getEntitiesOfType(type)) {
            entity.add(createComponent(RemoveComponent::class.java))
        }
    }
}
