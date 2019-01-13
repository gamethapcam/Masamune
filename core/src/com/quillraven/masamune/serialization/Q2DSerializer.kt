package com.quillraven.masamune.serialization

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.IntArray
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonWriter
import com.badlogic.gdx.utils.StreamUtils
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.component.ActionableComponent
import com.quillraven.masamune.ecs.component.Box2DComponent
import com.quillraven.masamune.ecs.component.InventoryComponent
import com.quillraven.masamune.ecs.component.TransformComponent
import com.quillraven.masamune.ecs.system.DEFAULT_ENTITY_ID
import com.quillraven.masamune.ecs.system.IdentifySystem
import com.quillraven.masamune.event.MapEvent
import com.quillraven.masamune.event.MapListener
import com.quillraven.masamune.map.EMapType
import com.quillraven.masamune.model.ObjectCfgMap
import com.quillraven.masamune.model.ObjectType
import java.io.StringWriter

private const val KEY_CURRENT_MAP = "currentMap"
private const val KEY_PLAYER_DATA = "playerData"
private const val KEY_PLAYER_ITEM_DATA = "playerItemData"
private const val KEY_MAP_DATA = "-data"
private const val KEY_PLAYER_X = "playerX"
private const val KEY_PLAYER_Y = "playerY"

private const val TAG = "Serializer"

class Q2DSerializer constructor(game: MainGame) : MapListener {
    private var playerCreated = false
    private val playerCfg = game.assetManager.get("cfg/character.json", ObjectCfgMap::class.java)[ObjectType.HERO]
    private val json = game.json
    private val jsonReader = JsonReader()

    private val idCmpMapper = game.cmpMapper.identify
    private val transfCmpMapper = game.cmpMapper.transform
    private val inventoryCmpMapper = game.cmpMapper.inventory
    private val playerEntityIDs = IntArray(0)

    private val gameStatePreference = Gdx.app.getPreferences("masamune")
    private val ecsEngine by lazy { game.ecsEngine }
    private val idSystem by lazy { ecsEngine.getSystem(IdentifySystem::class.java) }
    private val mapManager by lazy { game.mapManager }

    init {
        json.setSerializer(idSystem.javaClass, idSystem)
        game.gameEventManager.addMapListener(this)
    }

    private fun writeObjectStart() {
        json.setWriter(StringWriter())
        json.writeObjectStart()
    }

    private fun writeObjectEnd(): String {
        json.writeObjectEnd()
        StreamUtils.closeQuietly(json.writer)
        val result = json.writer.writer.toString()
        json.setWriter(null)
        return result
    }

    private fun writeComponentData(name: String, entity: Entity) {
        json.writeArrayStart(name)
        for (cmp in entity.components) {
            if (cmp is ActionableComponent) continue
            json.writeValue(cmp, null as Class<*>?)
        }
        json.writeArrayEnd()
    }

    fun saveGameState() {
        // save entity systems
        gameStatePreference.putString(idSystem.javaClass.name, json.toJson(idSystem))
        // playerEntityIDs stores the player related entity IDs like player, player-items, player-abilities, etc.
        // they are stored separately and are ignored in the saveMapData method that stores all remaining entities as mapdata
        playerEntityIDs.clear()
        gameStatePreference.putString(KEY_CURRENT_MAP, mapManager.currentMapType.name)
        savePlayerData()
        saveMapData()
        // store file
        gameStatePreference.flush()
    }

    private fun savePlayerData() {
        val playerEntity = ecsEngine.getSystem(IdentifySystem::class.java).getPlayerEntity()
        gameStatePreference.putString(KEY_PLAYER_DATA, getPlayerSaveString(playerEntity))
        gameStatePreference.putString(KEY_PLAYER_ITEM_DATA, getInventorySaveString(inventoryCmpMapper.get(playerEntity)))
    }

    private fun getPlayerSaveString(playerEntity: Entity): String {
        playerEntityIDs.add(idCmpMapper.get(playerEntity).id)

        writeObjectStart()
        writeComponentData("player", playerEntity)
        return jsonReader.parse(writeObjectEnd()).prettyPrint(JsonWriter.OutputType.minimal, 0)
    }

    private fun getInventorySaveString(inventoryCmp: InventoryComponent?): String {
        if (inventoryCmp == null) {
            Gdx.app.debug(TAG, "There is no player inventory to save")
            return ""
        }

        writeObjectStart()
        json.writeArrayStart("inventory")
        for (idx in 0 until inventoryCmp.items.size) {
            if (inventoryCmp.items[idx] != DEFAULT_ENTITY_ID) {
                val itemEntity = ecsEngine.getSystem(IdentifySystem::class.java).getEntityByID(inventoryCmp.items[idx])
                if (itemEntity == null) {
                    Gdx.app.error(TAG, "Trying to save invalid item of id ${inventoryCmp.items[idx]}")
                    continue
                }
                playerEntityIDs.add(inventoryCmp.items[idx])
                json.writeObjectStart()
                writeComponentData("slot-$idx", itemEntity)
                json.writeObjectEnd()
            }
        }
        json.writeArrayEnd()
        return jsonReader.parse(writeObjectEnd()).prettyPrint(JsonWriter.OutputType.minimal, 0)
    }

    private fun saveMapData() {
        gameStatePreference.putString("${mapManager.currentMapType.name}$KEY_MAP_DATA", getMapSaveString(ecsEngine.entities))
    }

    private fun getMapSaveString(entities: ImmutableArray<Entity>): String {
        if (entities.size() <= 0) {
            Gdx.app.debug(TAG, "There are no map entities to save")
            return ""
        }

        writeObjectStart()
        // save player location
        val playerEntity = ecsEngine.getSystem(IdentifySystem::class.java).getPlayerEntity()
        json.writeValue(KEY_PLAYER_X, transfCmpMapper.get(playerEntity).x)
        json.writeValue(KEY_PLAYER_Y, transfCmpMapper.get(playerEntity).y)

        // save map entities
        json.writeArrayStart("mapEntities")
        for (entity in entities) {
            // ignore player entities
            if (playerEntityIDs.contains(idCmpMapper.get(entity).id)) continue

            json.writeObjectStart()
            writeComponentData("entity-${idCmpMapper.get(entity).id}", entity)
            json.writeObjectEnd()
        }
        json.writeArrayEnd()
        return jsonReader.parse(writeObjectEnd()).prettyPrint(JsonWriter.OutputType.minimal, 0)
    }

    fun loadGameState() {
        // load entity system data
        if (gameStatePreference.contains(idSystem.javaClass.name)) {
            json.fromJson(idSystem.javaClass, gameStatePreference.getString(idSystem.javaClass.name))
        }
        // load player
        loadPlayerData()
        // load map
        mapManager.setMap(EMapType.valueOf(gameStatePreference.getString(KEY_CURRENT_MAP, EMapType.MAP01.name)))
    }

    override fun mapChanged(event: MapEvent) {
        if (gameStatePreference.contains("${mapManager.currentMapType.name}$KEY_MAP_DATA")) {
            // load map data from save file
            loadMapData()
        } else {
            // load map data from tmx tiled map
            mapManager.loadEntitiesForAllLayers()
            mapManager.setPlayerStartLocation()
        }
    }

    private fun loadPlayerData() {
        if (!playerCreated) {
            playerCreated = true
            if (gameStatePreference.contains(KEY_PLAYER_DATA)) {
                // load player data from save file
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
            } else {
                // create new player instance
                if (playerCfg == null) {
                    Gdx.app.error(TAG, "There is no player configuration defined")
                } else {
                    ecsEngine.createEntityFromConfig(playerCfg, widthScale = 0.75f, heightScale = 0.2f)
                }
            }
        }
    }

    private fun loadMapData() {
        val mapData = jsonReader.parse(gameStatePreference.getString("${mapManager.currentMapType.name}$KEY_MAP_DATA"))
        if (mapData != null) {
            val playerEntity = ecsEngine.getSystem(IdentifySystem::class.java).getPlayerEntity()
            val playerX = mapData.getFloat(KEY_PLAYER_X)
            val playerY = mapData.getFloat(KEY_PLAYER_Y)
            val transformCmp = playerEntity.getComponent(TransformComponent::class.java)
            playerEntity.getComponent(Box2DComponent::class.java).body.apply {
                setTransform(playerX + transformCmp.width * 0.5f, playerY + transformCmp.height * 0.5f, angle)
            }

            var iterator = mapData.child.next.next.child
            while (iterator != null) {
                val value = iterator.child.child
                iterator = iterator.next
                ecsEngine.createEntityFromConfig(value)
            }
        }
    }
}