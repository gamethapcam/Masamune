package com.quillraven.masamune.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.ChainShape
import com.quillraven.masamune.*

private const val TAG = "MapManager"

class MapManager constructor(game: MainGame) {
    private val ecsEngine = game.ecsEngine
    private val assetManger = game.assetManager
    private val gameEventManager = game.gameEventManager
    private val world = game.world
    private val rectVertices = FloatArray(8)

    fun setMap(type: EMapType) {
        val tiledMap = assetManger.get(type.filePath, TiledMap::class.java)

        loadCollisionObjects(tiledMap)
        loadCharacters(tiledMap)

        gameEventManager.mapEvent.newTiledMap = tiledMap
        gameEventManager.mapSignal.dispatch(gameEventManager.mapEvent)
    }

    private fun loadCollisionObjects(tiledMap: TiledMap) {
        val mapLayer = tiledMap.layers.get("collision")
        if (mapLayer == null) {
            Gdx.app.debug(TAG, "There is no collision layer")
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
                else -> Gdx.app.debug(TAG, "Unsupported collision map object of type ${mapObj.javaClass}")
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
        val mapLayer = tiledMap.layers.get("characters")
        if (mapLayer == null) {
            Gdx.app.debug(TAG, "There is no characters layer")
            return
        }

        for (mapObj in mapLayer.objects) {
            val charType = mapObj.properties.get("type", "", String::class.java)
            if (charType.isBlank()) {
                Gdx.app.debug(TAG, "Type is not defined for character tile ${mapObj.properties.get("id", Int::class.java)}")
                continue
            }

            if ("PLAYER" == charType) {
                ecsEngine.createPlayer(mapObj.properties.get("x", 0f, Float::class.java) * UNIT_SCALE, mapObj.properties.get("y", 0f, Float::class.java) * UNIT_SCALE)
            }
        }
    }
}