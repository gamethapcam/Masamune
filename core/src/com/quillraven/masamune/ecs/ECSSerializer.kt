package com.quillraven.masamune.ecs

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.component.ISerializableComponent

class ECSSerializer constructor(private val game: MainGame) : Json.Serializer<ECSEngine> {
    override fun write(json: Json, obj: ECSEngine, knownType: Class<*>?) {
        if (obj.entities.size() > 0) {
            json.writeArrayStart()
            for (entity in obj.entities) {
                json.writeArrayStart()
                for (cmp in entity.components) {
                    if (cmp is ISerializableComponent) {
                        json.writeObjectStart()
                        json.writeValue("cmpType", cmp.javaClass.name)
                        cmp.write(json)
                        json.writeObjectEnd()
                    }
                }
                json.writeArrayEnd()
            }
            json.writeArrayEnd()
        }
    }

    override fun read(json: Json, jsonData: JsonValue, type: Class<*>?): ECSEngine {
        var entityData = jsonData.child
        while (entityData != null) {
            val entity = game.ecsEngine.createEntity()

            var cmpData = entityData.child
            while (cmpData != null) {
                val cmp = game.ecsEngine.createComponent(Class.forName(cmpData.getString("cmpType")) as Class<Component>)
                (cmp as ISerializableComponent).read(cmpData, game)
                entity.add(cmp)
                cmpData = cmpData.next
            }

            game.ecsEngine.addEntity(entity)
            entityData = entityData.next
        }
        return game.ecsEngine
    }
}