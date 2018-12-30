package com.quillraven.masamune.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.ChainShape
import com.badlogic.gdx.utils.Array
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.UNIT_SCALE
import com.quillraven.masamune.model.CharacterCfgMap
import com.quillraven.masamune.model.ECharacterType

private const val TAG = "MapManager"

private const val LAYER_COLLISION = "collision"
private const val LAYER_CHARACTER = "character"
private const val LAYER_CAMERA_BOUNDARY = "cameraBoundary"

class MapManager constructor(game: MainGame) {
    private val ecsEngine = game.ecsEngine
    private val assetManger = game.assetManager
    private val gameEventManager = game.gameEventManager
    private val characterCfgMap = assetManger.get("cfg/character.json", CharacterCfgMap::class.java)
    private val b2dUtils = game.b2dUtils
    private val rectVertices = FloatArray(8)

    internal lateinit var currentMapType: EMapType
    private lateinit var currentTiledMap: TiledMap
    private val camBoundaryCache = Array<Rectangle>()
    private var numCamBoundaries = 0

    fun setMap(type: EMapType) {
        currentMapType = type
        currentTiledMap = assetManger.get(currentMapType.filePath, TiledMap::class.java)

        loadCollisionObjects()
        getCameraBoundaries()

        gameEventManager.dispatchMapEvent(currentMapType, currentTiledMap, currentTiledMap.properties.get("width", 0f, Float::class.java), currentTiledMap.properties.get("height", 0f, Float::class.java))
    }

    fun getCameraBoundaries(fill: Array<Rectangle>) {
        fill.clear()
        for (i in 0 until numCamBoundaries) {
            fill.add(camBoundaryCache.get(i))
        }
    }

    private fun loadCollisionObjects() {
        val mapLayer = currentTiledMap.layers.get(LAYER_COLLISION)
        if (mapLayer == null) {
            Gdx.app.debug(TAG, "There is no $LAYER_COLLISION layer")
            return
        }

        for (mapObj in mapLayer.objects) {
            when (mapObj) {
                is RectangleMapObject -> {
                    val rect = mapObj.rectangle
                    // bot-left
                    rectVertices[0] = 0f
                    rectVertices[1] = 0f
                    // top-left
                    rectVertices[2] = 0f
                    rectVertices[3] = rect.height
                    // top-right
                    rectVertices[4] = rect.width
                    rectVertices[5] = rect.height
                    // bot-right
                    rectVertices[6] = rect.width
                    rectVertices[7] = 0f
                    createCollisionObject(rect.x, rect.y, rectVertices, true)
                }
                is PolylineMapObject -> createCollisionObject(mapObj.polyline.x, mapObj.polyline.y, mapObj.polyline.vertices)
                is PolygonMapObject -> createCollisionObject(mapObj.polygon.x, mapObj.polygon.y, mapObj.polygon.vertices, true)
                else -> Gdx.app.debug(TAG, "Unsupported $LAYER_COLLISION map object of type ${mapObj.javaClass}")
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

        b2dUtils.createBody(BodyDef.BodyType.StaticBody, x * UNIT_SCALE, y * UNIT_SCALE, chainShape)

        for (i in vertices.indices) {
            vertices[i] /= UNIT_SCALE
        }
    }

    fun loadCharacters() {
        val mapLayer = currentTiledMap.layers.get(LAYER_CHARACTER)
        if (mapLayer == null) {
            Gdx.app.debug(TAG, "There is no $LAYER_CHARACTER layer")
            return
        }

        for (mapObj in mapLayer.objects) {
            val charTypeStr = mapObj.properties.get("type", "", String::class.java)
            if (charTypeStr.isBlank()) {
                Gdx.app.debug(TAG, "Type is not defined for $LAYER_CHARACTER tile ${mapObj.properties.get("id", Int::class.java)}")
                continue
            }

            try {
                val charCfg = characterCfgMap[ECharacterType.valueOf(charTypeStr)]
                if (charCfg == null) {
                    Gdx.app.debug(TAG, "There is no character cfg of type  $charTypeStr defined for $LAYER_CHARACTER tile ${mapObj.properties.get("id", Int::class.java)}")
                    continue
                }

                ecsEngine.createEntityFromConfig(charCfg, mapObj.properties.get("x", 0f, Float::class.java) * UNIT_SCALE, mapObj.properties.get("y", 0f, Float::class.java) * UNIT_SCALE)
            } catch (e: IllegalArgumentException) {
                Gdx.app.error(TAG, "Invalid Type $charTypeStr for $LAYER_CHARACTER tile ${mapObj.properties.get("id", Int::class.java)}")
                continue
            }
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
}
