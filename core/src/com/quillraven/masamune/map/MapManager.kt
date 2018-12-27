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
import com.quillraven.masamune.*
import com.quillraven.masamune.model.CharacterCfgMap
import com.quillraven.masamune.model.ECharactedType

private const val TAG = "MapManager"

private const val LAYER_COLLISION = "collision"
private const val LAYER_CHARACTER = "character"
private const val LAYER_CAMERA_BOUNDARY = "cameraBoundary"

class MapManager constructor(game: MainGame) {
    private val ecsEngine = game.ecsEngine
    private val assetManger = game.assetManager
    private val gameEventManager = game.gameEventManager
    private val characterCfgMap = assetManger.get("cfg/character.json", CharacterCfgMap::class.java)
    private val world = game.world
    private val rectVertices = FloatArray(8)

    private val camBoundaryCache = Array<Rectangle>()
    private var numCamBoundaries = 0

    fun setMap(type: EMapType) {
        val tiledMap = assetManger.get(type.filePath, TiledMap::class.java)

        loadCollisionObjects(tiledMap)
        loadCharacters(tiledMap)
        getCameraBoundaries(tiledMap)

        gameEventManager.dispatchMapEvent(type, tiledMap, tiledMap.properties.get("width", 0f, Float::class.java), tiledMap.properties.get("height", 0f, Float::class.java))
    }

    fun getCameraBoundaries(fill: Array<Rectangle>) {
        fill.clear()
        for (i in 0 until numCamBoundaries) {
            fill.add(camBoundaryCache.get(i))
        }
    }

    private fun loadCollisionObjects(tiledMap: TiledMap) {
        val mapLayer = tiledMap.layers.get(LAYER_COLLISION)
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
        resetBodyAndFixtureDef()
        bodyDef.type = BodyDef.BodyType.StaticBody
        bodyDef.position.set(x * UNIT_SCALE, y * UNIT_SCALE)
        bodyDef.fixedRotation = true
        val body = world.createBody(bodyDef)

        for (i in vertices.indices) {
            vertices[i] *= UNIT_SCALE
        }

        val chainShape = ChainShape()
        if (loop) {
            chainShape.createLoop(vertices)
        } else {
            chainShape.createChain(vertices)
        }
        fixtureDef.shape = chainShape
        fixtureDef.isSensor = false
        body.createFixture(fixtureDef)
        chainShape.dispose()

        for (i in vertices.indices) {
            vertices[i] /= UNIT_SCALE
        }
    }

    private fun loadCharacters(tiledMap: TiledMap) {
        val mapLayer = tiledMap.layers.get(LAYER_CHARACTER)
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
                val charType = characterCfgMap.get(ECharactedType.valueOf(charTypeStr))
                if (charType == null) {
                    Gdx.app.debug(TAG, "There is no character cfg of type  $charTypeStr defined for $LAYER_CHARACTER tile ${mapObj.properties.get("id", Int::class.java)}")
                    continue
                }

                ecsEngine.createCharacter(mapObj.properties.get("x", 0f, Float::class.java) * UNIT_SCALE, mapObj.properties.get("y", 0f, Float::class.java) * UNIT_SCALE, charType)
            } catch (e: IllegalArgumentException) {
                Gdx.app.error(TAG, "Invalid Type $charTypeStr for $LAYER_CHARACTER tile ${mapObj.properties.get("id", Int::class.java)}")
                continue
            }
        }
    }

    private fun getCameraBoundaries(tiledMap: TiledMap) {
        numCamBoundaries = 0

        val mapLayer = tiledMap.layers.get(LAYER_CAMERA_BOUNDARY)
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