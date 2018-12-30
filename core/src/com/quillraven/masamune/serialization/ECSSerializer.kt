package com.quillraven.masamune.serialization

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.ecs.component.RenderComponent

class ECSSerializer constructor(private val game: MainGame) : Json.Serializer<ECSEngine> {
    private val classKey = "class"

    private val transformCmpMapper = game.cmpMapper.transform
    private val b2dCmpMapper = game.cmpMapper.box2D
    private val renderCmpMapper = game.cmpMapper.render

    override fun write(json: Json, obj: ECSEngine, knownType: Class<*>?) {
        if (obj.entities.size() > 0) {
            json.writeArrayStart()
            for (entity in obj.entities) {
                json.writeArrayStart()
                for (cmp in entity.components) {
                    json.writeObjectStart()
                    json.writeValue(classKey, cmp.javaClass.simpleName)
                    json.writeFields(cmp)
                    json.writeObjectEnd()
                }
                json.writeArrayEnd()
            }
            json.writeArrayEnd()
        }
    }

    override fun read(json: Json, jsonData: JsonValue, type: Class<*>?): ECSEngine {
        val componentPackage = "${RenderComponent::class.java.`package`.name}."
        var entityData = jsonData.child
        while (entityData != null) {
            val entity = game.ecsEngine.createEntity()

            var cmpData = entityData.child
            while (cmpData != null) {
                @Suppress("UNCHECKED_CAST")
                val cmp = game.ecsEngine.createComponent(Class.forName("$componentPackage${cmpData.getString(classKey)}") as Class<Component>)
                json.readFields(cmp, cmpData)
                entity.add(cmp)
                cmpData = cmpData.next
            }

            // create box2d body if needed
            val b2dCmp = b2dCmpMapper.get(entity)
            val transformCmp = transformCmpMapper.get(entity)
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
            val renderCmp = renderCmpMapper.get(entity)
            if (renderCmp != null && !renderCmp.texture.isBlank()) {
                renderCmp.sprite = game.spriteCache.getSprite(renderCmp.texture)
            }

            game.ecsEngine.addEntity(entity)
            entityData = entityData.next
        }
        return game.ecsEngine
    }
}