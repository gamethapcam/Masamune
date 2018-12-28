package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Array
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.component.CameraComponent
import com.quillraven.masamune.event.MapEvent

class CameraSystem constructor(game: MainGame) : IteratingSystem(Family.all(CameraComponent::class.java).get()), Listener<MapEvent> {
    private val camera = game.gameViewPort.camera
    private val b2dCmpMapper = game.cmpMapper.box2D

    private val camBoundaries = Array<Rectangle>()
    private val mapBoundary = Rectangle(0f, 0f, 0f, 0f)
    private val currentBoundary = Rectangle(0f, 0f, 0f, 0f)
    private val mapManager by lazy { game.mapManager }

    init {
        game.gameEventManager.addMapEventListener(this)
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val b2dCmp = b2dCmpMapper.get(entity)

        if (!currentBoundary.contains(b2dCmp.interpolatedX, b2dCmp.interpolatedY)) {
            // find new boundary
            // default is map boundary
            currentBoundary.set(mapBoundary)
            for (rect in camBoundaries) {
                if (rect.contains(b2dCmp.interpolatedX, b2dCmp.interpolatedY)) {
                    currentBoundary.set(rect)
                    break
                }
            }
        }

        val camW = camera.viewportWidth * 0.5f
        val camH = camera.viewportHeight * 0.5f
        camera.position.apply {
            x = MathUtils.clamp(b2dCmp.interpolatedX, currentBoundary.x + camW, currentBoundary.x + currentBoundary.width - camW)
            y = MathUtils.clamp(b2dCmp.interpolatedY, currentBoundary.y + camH, currentBoundary.y + currentBoundary.height - camH)
        }
    }

    override fun receive(signal: Signal<MapEvent>?, obj: MapEvent) {
        currentBoundary.set(0f, 0f, 0f, 0f)
        mapBoundary.set(0f, 0f, obj.width, obj.height)
        mapManager.getCameraBoundaries(camBoundaries)
    }
}