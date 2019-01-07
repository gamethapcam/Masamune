package com.quillraven.masamune.serialization

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.StringBuilder
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.event.MapEvent
import com.quillraven.masamune.event.MapListener
import com.quillraven.masamune.map.EMapType

private const val KEY_CURRENT_MAP = "currentMap"
private const val KEY_MAP_DATA = "-data"

class Q2DSerializer constructor(game: MainGame) : MapListener {
    private val json = game.json
    private val jsonReader = JsonReader()
    private val keyBuffer = StringBuilder()
    private val mapDataBuffer = StringBuilder()
    private val gameStatePreference = Gdx.app.getPreferences("masamune")
    private val ecsEngine by lazy { game.ecsEngine }
    private val mapManager by lazy { game.mapManager }

    init {
        json.setSerializer(ECSEngine::class.java, ECSSerializer(game))
        game.gameEventManager.addMapListener(this)
    }

    fun saveGameState() {
        gameStatePreference.putString(KEY_CURRENT_MAP, mapManager.currentMapType.name)
        gameStatePreference.putString("${mapManager.currentMapType.name}$KEY_MAP_DATA", json.toJson(ecsEngine))
//        gameStatePreference.flush()
    }

    fun loadGameState() {
        val currentMapType = EMapType.valueOf(gameStatePreference.getString(KEY_CURRENT_MAP, EMapType.MAP01.name))
        mapManager.setMap(currentMapType)
    }

    private fun readMapData(mapDataKey: String): JsonValue {
        val mapDataStr = gameStatePreference.getString(mapDataKey)
        mapDataBuffer.setLength(0)
        mapDataBuffer.append(mapDataStr)
        return jsonReader.parse(mapDataBuffer.chars, 0, mapDataStr.length)
    }

    private fun loadMapEntities(mapType: EMapType) {
        keyBuffer.setLength(0)
        keyBuffer.append(mapType.name).append(KEY_MAP_DATA)
        val mapDataKey = keyBuffer.toString()
        if (gameStatePreference.contains(mapDataKey)) {
            json.readValue(ECSEngine::class.java, null, readMapData(mapDataKey))
        } else {
            mapManager.loadCharacters()
            mapManager.loadObjects()
            mapManager.loadItems()
        }
    }

    //TODO
    /*private fun getCharacterTypeFromEntityData(cmpData: JsonValue): ECharacterType {
        var iterator: JsonValue? = cmpData
        while (iterator != null) {
            val value = iterator
            iterator = iterator.next

            if (value.getString(CLASS_KEY) == CharacterComponent::class.java.name) {
                return ECharacterType.valueOf(value.getString("type"))
            }
        }
        return ECharacterType.UNDEFINED
    }*/

    override fun mapChanged(event: MapEvent) {
        loadMapEntities(event.newType)

        if (event.oldType != EMapType.UNDEFINED) {
            keyBuffer.setLength(0)
            keyBuffer.append(event.oldType.name).append(KEY_MAP_DATA)
            val oldMapDataKey = keyBuffer.toString()
            if (gameStatePreference.contains(oldMapDataKey)) {
                var entityDataIterator = readMapData(oldMapDataKey).child
                while (entityDataIterator != null) {
                    val cmpData = entityDataIterator.child
                    entityDataIterator = entityDataIterator.next

                    //TODO
                    /*val charType = getCharacterTypeFromEntityData(cmpData)
                    if (charType == ECharacterType.UNDEFINED) {
                        continue
                    }

                    ecsEngine.initCharacterEntityFromConfig(charType, cmpData)*/
                }
            }
        }
    }
}