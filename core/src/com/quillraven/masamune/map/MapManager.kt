package com.quillraven.masamune.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.ChainShape
import com.badlogic.gdx.utils.Array
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.UNIT_SCALE
import com.quillraven.masamune.ecs.EntityType
import com.quillraven.masamune.model.ObjectCfgMap
import com.quillraven.masamune.model.ObjectType

private const val TAG = "MapManager"

private const val LAYER_COLLISION = "collision"
private const val LAYER_CHARACTER = "character"
private const val LAYER_CAMERA_BOUNDARY = "cameraBoundary"
private const val LAYER_OBJECT = "object"
private const val LAYER_ITEM = "item"
private const val GROUND_USER_DATA = "ground"

class MapManager constructor(game: MainGame) {
    private val ecsEngine = game.ecsEngine
    private val assetManger = game.assetManager
    private val gameEventManager = game.gameEventManager
    private val gameSerializer = game.serializer
    private val characterCfgMap = assetManger.get("cfg/character.json", ObjectCfgMap::class.java)
    private val objectCfgMap = assetManger.get("cfg/object.json", ObjectCfgMap::class.java)
    private val itemCfgMap = assetManger.get("cfg/item.json", ObjectCfgMap::class.java)
    private val b2dUtils = game.b2dUtils

    private val bgdLayers = Array<TiledMapTileLayer>()
    private val fgdLayers = Array<TiledMapTileLayer>()

    internal var currentMapType = EMapType.UNDEFINED
    private lateinit var currentTiledMap: TiledMap
    private val camBoundaryCache = Array<Rectangle>()
    private var numCamBoundaries = 0

    fun setMap(type: EMapType) {
        if (currentMapType == type) {
            Gdx.app.error(TAG, "Trying to set the same map twice: $currentMapType")
            return
        } else if (currentMapType != EMapType.UNDEFINED) {
            // save current map state
            gameSerializer.saveGameState()
            // unload current map
            destroyCollisionObjects()
            destroyCharacters()
            destroyObjects()
        }

        val previousMapType = currentMapType
        currentMapType = type
        currentTiledMap = assetManger.get(currentMapType.filePath, TiledMap::class.java)

        loadCollisionObjects()
        getCameraBoundaries()
        getRenderLayers()

        gameEventManager.dispatchMapChanged(previousMapType, currentMapType, currentTiledMap,
                currentTiledMap.properties.get("width", 0f, Float::class.java),
                currentTiledMap.properties.get("height", 0f, Float::class.java),
                bgdLayers, fgdLayers)
    }

    fun getCameraBoundaries(fill: Array<Rectangle>) {
        fill.clear()
        for (i in 0 until numCamBoundaries) {
            fill.add(camBoundaryCache.get(i))
        }
    }

    private fun destroyCollisionObjects() {
        b2dUtils.destroyBodies(GROUND_USER_DATA)
    }

    private fun loadCollisionObjects() {
        val mapLayer = currentTiledMap.layers.get(LAYER_COLLISION)
        if (mapLayer == null) {
            Gdx.app.debug(TAG, "There is no $LAYER_COLLISION layer")
            return
        }

        for (mapObj in mapLayer.objects) {
            when (mapObj) {
                is RectangleMapObject -> createCollisionObject(mapObj.rectangle.x, mapObj.rectangle.y, b2dUtils.getRectVertices(mapObj.rectangle.width, mapObj.rectangle.height), true)
                is PolylineMapObject -> createCollisionObject(mapObj.polyline.x, mapObj.polyline.y, mapObj.polyline.vertices)
                is PolygonMapObject -> createCollisionObject(mapObj.polygon.x, mapObj.polygon.y, mapObj.polygon.vertices, true)
                else -> Gdx.app.error(TAG, "Unsupported $LAYER_COLLISION map object of type ${mapObj.javaClass}")
            }
        }
    }

    private fun createCollisionObject(x: Float, y: Float, vertices: FloatArray, loop: Boolean = false) {
        for (i in vertices.indices) {
            vertices[i] *= UNIT_SCALE
        }

        val chainShape = ChainShape()
        if (loop) {
            chainShape.createLoop(vertices)
        } else {
            chainShape.createChain(vertices)
        }

        b2dUtils.createBody(BodyDef.BodyType.StaticBody, x * UNIT_SCALE, y * UNIT_SCALE, chainShape, GROUND_USER_DATA)

        for (i in vertices.indices) {
            vertices[i] /= UNIT_SCALE
        }
    }

    private fun getCameraBoundaries() {
        numCamBoundaries = 0

        val mapLayer = currentTiledMap.layers.get(LAYER_CAMERA_BOUNDARY)
        if (mapLayer == null) {
            Gdx.app.debug(TAG, "There is no $LAYER_CAMERA_BOUNDARY layer defined")
            return
        }

        for (mapObj in mapLayer.objects) {
            if (mapObj is RectangleMapObject) {
                if (camBoundaryCache.size <= numCamBoundaries) {
                    camBoundaryCache.add(Rectangle(0f, 0f, 0f, 0f))
                }

                camBoundaryCache.get(numCamBoundaries).set(mapObj.rectangle).apply {
                    x *= UNIT_SCALE
                    y *= UNIT_SCALE
                    width *= UNIT_SCALE
                    height *= UNIT_SCALE
                }
                ++numCamBoundaries
            } else {
                Gdx.app.error(TAG, "There is a non-rectangle camera boundary area")
            }
        }
    }

    fun loadEntitiesForAllLayers() {
        loadEntitiesFromLayer(LAYER_CHARACTER, characterCfgMap, 0.75f, 0.2f)
        loadEntitiesFromLayer(LAYER_OBJECT, objectCfgMap)
        loadEntitiesFromLayer(LAYER_ITEM, itemCfgMap, 0.8f, 0.3f)
    }

    private fun loadEntitiesFromLayer(layerName: String, objCfgMap: ObjectCfgMap, widthScale: Float = 1f, heightScale: Float = 1f) {
        val mapLayer = currentTiledMap.layers.get(layerName)
        if (mapLayer == null) {
            Gdx.app.debug(TAG, "There is no $layerName layer")
            return
        }

        for (mapObj in mapLayer.objects) {
            val typeStr = mapObj.properties.get("type", "", String::class.java)
            if (typeStr.isBlank()) {
                Gdx.app.error(TAG, "Type is not defined for $layerName tile ${mapObj.properties.get("id", Int::class.java)}")
                continue
            }

            try {
                val cfg = objCfgMap[ObjectType.valueOf(typeStr)]
                if (cfg == null) {
                    Gdx.app.error(TAG, "There is no cfg of type  $typeStr defined for $layerName tile ${mapObj.properties.get("id", Int::class.java)}")
                    continue
                }

                val posX = mapObj.properties.get("x", 0f, Float::class.java) * UNIT_SCALE
                val posY = mapObj.properties.get("y", 0f, Float::class.java) * UNIT_SCALE
                ecsEngine.createEntityFromConfig(cfg, posX, posY, widthScale, heightScale)
            } catch (e: IllegalArgumentException) {
                Gdx.app.error(TAG, "Invalid Type $typeStr for $layerName tile ${mapObj.properties.get("id", Int::class.java)}")
                continue
            }
        }
    }

    private fun destroyCharacters() {
        ecsEngine.destroyEntitiesOfType(EntityType.CHARACTER)
    }

    private fun destroyObjects() {
        ecsEngine.destroyEntitiesOfType(EntityType.OBJECT)
    }

    private fun destroyItems() {
        //TODO
    }

    private fun getRenderLayers() {
        bgdLayers.clear()
        fgdLayers.clear()
        for (layer in currentTiledMap.layers) {
            if (layer is TiledMapTileLayer) {
                if (layer.name.startsWith("fore")) {
                    fgdLayers.add(layer)
                } else {
                    bgdLayers.add(layer)
                }
            }
        }
    }
}
