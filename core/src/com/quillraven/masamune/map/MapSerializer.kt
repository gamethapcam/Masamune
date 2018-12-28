package com.quillraven.masamune.map

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.quillraven.masamune.MainGame

class MapSerializer constructor(private val game: MainGame) : Json.Serializer<MapManager> {
    override fun write(json: Json, obj: MapManager, knownType: Class<*>?) {
        json.writeObjectStart()
        json.writeValue("currentMap", obj.currentMapType.name)
        json.writeObjectEnd()
    }

    override fun read(json: Json, jsonData: JsonValue, type: Class<*>?): MapManager {
        game.mapManager.setMap(EMapType.valueOf(jsonData.getString("currentMap", EMapType.MAP01.name)))
        return game.mapManager
    }
}