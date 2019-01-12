package com.quillraven.masamune.serialization

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.*
import com.badlogic.gdx.utils.IntArray
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.component.ActionableComponent
import com.quillraven.masamune.ecs.component.Box2DComponent
import com.quillraven.masamune.ecs.component.InventoryComponent
import com.quillraven.masamune.ecs.component.TransformComponent
import com.quillraven.masamune.ecs.system.DEFAULT_ENTITY_ID
import com.quillraven.masamune.ecs.system.IdentifySystem
import com.quillraven.masamune.ecs.system.InventorySystem
import com.quillraven.masamune.event.MapEvent
import com.quillraven.masamune.event.MapListener
import com.quillraven.masamune.map.EMapType
import java.io.StringWriter

private const val KEY_CURRENT_MAP = "currentMap"
private const val KEY_PLAYER_DATA = "playerData"
private const val KEY_PLAYER_ITEM_DATA = "playerItemData"
private const val KEY_MAP_DATA = "-data"
private const val KEY_PLAYER_X = "playerX"
private const val KEY_PLAYER_Y = "playerY"

private const val TAG = "Serializer"

class Q2DSerializer constructor(game: MainGame) : MapListener {
    private val json = game.json
    private val jsonReader = JsonReader()

    private val idCmpMapper = game.cmpMapper.identify
    private val transfCmpMapper = game.cmpMapper.transform
    private val playerEntityIDs = IntArray(0)

    private val gameStatePreference = Gdx.app.getPreferences("masamune")
    private val ecsEngine by lazy { game.ecsEngine }
    private val mapManager by lazy { game.mapManager }

    init {
        json.setSerializer(IdentifySystem::class.java, ecsEngine.getSystem(IdentifySystem::class.java))
        game.gameEventManager.addMapListener(this)
    }

    fun saveGameState() {
        playerEntityIDs.clear()
        gameStatePreference.putString(KEY_CURRENT_MAP, mapManager.currentMapType.name)
        savePlayerData()
        saveMapData()
        for (system in ecsEngine.systems) {
            if (system is Json.Serializer<*>) {
                gameStatePreference.putString(system.javaClass.name, json.toJson(system))
            }
        }
        playerEntityIDs.clear()
        gameStatePreference.flush()
    }

    private fun saveMapData() {
        gameStatePreference.putString("${mapManager.currentMapType.name}$KEY_MAP_DATA", getEntitiesSaveString(ecsEngine.entities))
    }

    private fun getEntitiesSaveString(entities: ImmutableArray<Entity>): String {
        if (entities.size() <= 0) {
            Gdx.app.debug(TAG, "There are no map entities to save")
            return ""
        }

        val buffer = StringWriter()
        json.setWriter(buffer)
        json.writeObjectStart()

        val playerEntity = ecsEngine.getSystem(IdentifySystem::class.java).getPlayerEntity()
        if (playerEntity != null) {
            json.writeValue(KEY_PLAYER_X, transfCmpMapper.get(playerEntity).x)
            json.writeValue(KEY_PLAYER_Y, transfCmpMapper.get(playerEntity).y)
        }

        json.writeArrayStart("mapEntities")
        for (entity in entities) {
            if (playerEntityIDs.contains(idCmpMapper.get(entity).id)) continue

            json.writeObjectStart()
            json.writeArrayStart("entity-${idCmpMapper.get(entity).id}")
            for (cmp in entity.components) {
                if (cmp is ActionableComponent) continue
                json.writeValue(cmp, null as Class<*>?)
            }
            json.writeArrayEnd()
            json.writeObjectEnd()
        }
        json.writeArrayEnd()
        json.writeObjectEnd()
        StreamUtils.closeQuietly(buffer)
        json.setWriter(null)
        val result = buffer.toString()
        return jsonReader.parse(result).prettyPrint(JsonWriter.OutputType.minimal, 0)
    }

    private fun savePlayerData() {
        val playerEntity = ecsEngine.getSystem(IdentifySystem::class.java).getPlayerEntity()
        gameStatePreference.putString(KEY_PLAYER_DATA, getEntitySaveString(playerEntity))
        gameStatePreference.putString(KEY_PLAYER_ITEM_DATA, if (playerEntity == null) "" else getInventorySaveString(ecsEngine.getSystem(InventorySystem::class.java).getInventory(playerEntity)))
    }

    private fun getEntitySaveString(playerEntity: Entity?): String {
        if (playerEntity == null) {
            Gdx.app.debug(TAG, "There is no player entity to save")
            return ""
        }

        playerEntityIDs.add(idCmpMapper.get(playerEntity).id)

        val buffer = StringWriter()
        json.setWriter(buffer)
        json.writeObjectStart()
        json.writeArrayStart("player")
        for (cmp in playerEntity.components) {
            json.writeValue(cmp, null as Class<*>?)
        }
        json.writeArrayEnd()
        json.writeObjectEnd()
        StreamUtils.closeQuietly(buffer)
        json.setWriter(null)
        val result = buffer.toString()
        return jsonReader.parse(result).prettyPrint(JsonWriter.OutputType.minimal, 0)
    }


    private fun getInventorySaveString(inventoryCmp: InventoryComponent?): String {
        if (inventoryCmp == null) {
            Gdx.app.debug(TAG, "There is no player inventory to save")
            return ""
        }

        val buffer = StringWriter()
        json.setWriter(buffer)
        json.writeObjectStart()
        json.writeArrayStart("inventory")
        for (idx in 0 until inventoryCmp.items.size) {
            if (inventoryCmp.items[idx] != DEFAULT_ENTITY_ID) {
                val itemEntity = ecsEngine.getSystem(IdentifySystem::class.java).getEntityByID(inventoryCmp.items[idx])
                if (itemEntity == null) {
                    Gdx.app.error(TAG, "Trying to save an invalid item with id ${inventoryCmp.items[idx]}")
                    continue
                }

                playerEntityIDs.add(inventoryCmp.items[idx])

                json.writeObjectStart()
                json.writeArrayStart("slot-$idx")
                for (cmp in itemEntity.components) {
                    json.writeValue(cmp, null as Class<*>?)
                }
                json.writeArrayEnd()
                json.writeObjectEnd()
            }
        }
        json.writeArrayEnd()
        json.writeObjectEnd()
        StreamUtils.closeQuietly(buffer)
        json.setWriter(null)
        val result = buffer.toString()
        return jsonReader.parse(result).prettyPrint(JsonWriter.OutputType.minimal, 0)
    }

    fun loadGameState() {
        mapManager.setMap(EMapType.valueOf(gameStatePreference.getString(KEY_CURRENT_MAP, EMapType.MAP01.name)))
    }

    override fun mapChanged(event: MapEvent) {
        if (gameStatePreference.contains("${mapManager.currentMapType.name}$KEY_MAP_DATA")) {
            loadPlayerData()
            loadMapData()
            for (system in ecsEngine.systems) {
                if (system is Json.Serializer<*>) {
                    json.fromJson(system.javaClass, gameStatePreference.getString(system.javaClass.name))
                }
            }
        } else {
            mapManager.loadEntitiesForAllLayers()
            mapManager.setPlayerStartLocation()
        }
    }

    private fun loadMapData() {
        val mapData = jsonReader.parse(gameStatePreference.getString("${mapManager.currentMapType.name}$KEY_MAP_DATA"))
        if (mapData != null) {
            val playerEntity = ecsEngine.getSystem(IdentifySystem::class.java).getPlayerEntity()
            if (playerEntity != null) {
                val playerX = mapData.getFloat(KEY_PLAYER_X)
                val playerY = mapData.getFloat(KEY_PLAYER_Y)
                val transformCmp = playerEntity.getComponent(TransformComponent::class.java)
                playerEntity.getComponent(Box2DComponent::class.java).body.apply {
                    setTransform(playerX + transformCmp.width * 0.5f, playerY + transformCmp.height * 0.5f, angle)
                }
            }

            var iterator = mapData.child.next.next.child
            while (iterator != null) {
                val value = iterator.child.child
                iterator = iterator.next
                ecsEngine.createEntityFromConfig(value)
            }
        }
    }


    private fun loadPlayerData() {
        val playerEntity = ecsEngine.getSystem(IdentifySystem::class.java).getPlayerEntity()
        if (playerEntity != null) {
            // player data already loaded
            return
        }

        val playerData = jsonReader.parse(gameStatePreference.getString(KEY_PLAYER_DATA))
        ecsEngine.createEntityFromConfig(playerData.child.child)
        val inventoryData = jsonReader.parse(gameStatePreference.getString(KEY_PLAYER_ITEM_DATA))
        if (inventoryData != null) {
            var iterator = inventoryData.child.child
            while (iterator != null) {
                val value = iterator.child.child
                iterator = iterator.next
                ecsEngine.createEntityFromConfig(value)
            }
        }
    }
}