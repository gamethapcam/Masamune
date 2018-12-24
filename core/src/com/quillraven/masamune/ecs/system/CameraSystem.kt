package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Array
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.UNIT_SCALE
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.ecs.component.CameraComponent
import com.quillraven.masamune.event.MapEvent

private const val TAG = "CameraSystem"

class CameraSystem constructor(game: MainGame, private val camera: Camera = game.gameViewPort.camera) : IteratingSystem(Family.all(CameraComponent::class.java).get()), Listener<MapEvent> {
    private val rectCache = Array<Rectangle>()
    private var numBoundaries = 0
    private val mapBoundaries = Rectangle(0f, 0f, 0f, 0f)
    private val currentBoundaries = Rectangle(0f, 0f, 0f, 0f)

    init {
        game.gameEventManager.mapSignal.add(this)
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val b2dCmp = (engine as ECSEngine).box2DMapper.get(entity)

        if (!currentBoundaries.contains(b2dCmp.interpolatedX, b2dCmp.interpolatedY)) {
            // find new boundary
            // default is map boundary
            currentBoundaries.set(mapBoundaries)
            for (i in 0..numBoundaries - 1) {
                if (rectCache.get(i).contains(b2dCmp.interpolatedX, b2dCmp.interpolatedY)) {
                    currentBoundaries.set(rectCache.get(i))
                    break
                }
            }
        }

        val camW = camera.viewportWidth * 0.5f
        val camH = camera.viewportHeight * 0.5f
        camera.position.apply {
            x = MathUtils.clamp(b2dCmp.interpolatedX, currentBoundaries.x + camW, currentBoundaries.x + currentBoundaries.width - camW)
            y = MathUtils.clamp(b2dCmp.interpolatedY, currentBoundaries.y + camH, currentBoundaries.y + currentBoundaries.height - camH)
        }
    }

    override fun receive(signal: Signal<MapEvent>?, `object`: MapEvent?) {
        val tiledMap = `object`!!.newTiledMap!!

        currentBoundaries.set(0f, 0f, 0f, 0f)
        mapBoundaries.set(0f, 0f, tiledMap.properties.get("width", 10000f, Float::class.java), tiledMap.properties.get("height", 10000f, Float::class.java))

        val mapLayer = tiledMap.layers.get("cameraBoundaries")
        if (mapLayer == null) {
            Gdx.app.debug(TAG, "There is no cameraBoundaries layer defined")
            return
        }

        numBoundaries = 0
        for (mapObj in mapLayer.objects) {
            if (mapObj is RectangleMapObject) {
                if (rectCache.size <= numBoundaries) {
                    rectCache.add(Rectangle(0f, 0f, 0f, 0f))
                }

                rectCache.get(numBoundaries).set(mapObj.rectangle).apply {
                    x *= UNIT_SCALE
                    y *= UNIT_SCALE
                    width *= UNIT_SCALE
                    height *= UNIT_SCALE
                }
                ++numBoundaries
            } else {
                Gdx.app.error(TAG, "There is a non-rectangle camera boundary area")
            }
        }
    }
}