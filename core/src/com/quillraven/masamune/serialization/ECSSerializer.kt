package com.quillraven.masamune.serialization

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.ECSEngine

internal const val CLASS_KEY = "class"

class ECSSerializer constructor(private val game: MainGame) : Json.Serializer<ECSEngine> {
    override fun write(json: Json, obj: ECSEngine, knownType: Class<*>?) {
        if (obj.entities.size() > 0) {
            json.writeArrayStart()
            for (entity in obj.entities) {
                json.writeArrayStart()
                for (cmp in entity.components) {
                    json.writeObjectStart()
                    json.writeValue(CLASS_KEY, cmp.javaClass.name)
                    json.writeFields(cmp)
                    json.writeObjectEnd()
                }
                json.writeArrayEnd()
            }
            json.writeArrayEnd()
        }
    }

    override fun read(json: Json, jsonData: JsonValue, type: Class<*>?): ECSEngine {
        var entityData = jsonData.child
        while (entityData != null) {
            game.ecsEngine.createEntityFromConfig(entityData.child)
            entityData = entityData.next
        }
        return game.ecsEngine
    }
}