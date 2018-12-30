package com.quillraven.masamune.ecs

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.JsonValue
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.component.RenderComponent
import com.quillraven.masamune.ecs.system.*
import com.quillraven.masamune.serialization.CLASS_KEY

class ECSEngine : PooledEngine(), Disposable {
    private val game = Gdx.app.applicationListener as MainGame

    init {
        addSystem(PlayerInputSystem(game))
        addSystem(Box2DSystem(game))
        addSystem(CameraSystem(game)) // add AFTER box2d system to use the calculated interpolated values
        addSystem(RenderFlipSystem(game))
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

    // cmpData is a json array of serialized component data
    fun createEntityFromConfig(cmpData: JsonValue, posX: Float = 0f, posY: Float = 0f) {
        val componentPackage = "${RenderComponent::class.java.`package`.name}."
        val entity = createEntity()

        var iterator: JsonValue? = cmpData
        while (iterator != null) {
            @Suppress("UNCHECKED_CAST")
            val cmp = createComponent(Class.forName("$componentPackage${iterator.getString(CLASS_KEY)}") as Class<Component>)
            game.json.readFields(cmp, iterator)
            entity.add(cmp)
            iterator = iterator.next
        }

        // create box2d body if needed
        val b2dCmp = game.cmpMapper.box2D.get(entity)
        val transformCmp = game.cmpMapper.transform.get(entity)
        if (transformCmp != null && (posX != 0f || posY != 0f)) {
            // set special location for entity if specified
            transformCmp.x = posX
            transformCmp.y = posY
        }
        if (b2dCmp != null && transformCmp != null) {
            val polygonShape = PolygonShape()
            polygonShape.setAsBox(transformCmp.width * 0.5f, transformCmp.height * 0.5f)
            b2dCmp.body = game.b2dUtils.createBody(
                    BodyDef.BodyType.values()[b2dCmp.type],
                    transformCmp.x + transformCmp.width * 0.5f,
                    transformCmp.y + transformCmp.height * 0.5f,
                    polygonShape
            )
        }

        // set sprite if needed
        val renderCmp = game.cmpMapper.render.get(entity)
        if (renderCmp != null && !renderCmp.texture.isBlank()) {
            renderCmp.sprite = game.spriteCache.getSprite(renderCmp.texture)
        }

        addEntity(entity)
    }
}
