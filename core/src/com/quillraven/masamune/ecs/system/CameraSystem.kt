package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Array
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.component.CameraComponent
import com.quillraven.masamune.ecs.component.TransformComponent
import com.quillraven.masamune.event.MapEvent
import com.quillraven.masamune.event.MapListener

class CameraSystem constructor(game: MainGame) : IteratingSystem(Family.all(CameraComponent::class.java, TransformComponent::class.java).get()), MapListener {
    private val camera = game.gameViewPort.camera
    private val transformCmpMapper = game.cmpMapper.transform

    private val camBoundaries = Array<Rectangle>()
    private val mapBoundary = Rectangle(0f, 0f, 0f, 0f)
    private val currentBoundary = Rectangle(0f, 0f, 0f, 0f)
    private val mapManager by lazy { game.mapManager }

    init {
        game.gameEventManager.addMapListener(this)
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val transformCmp = transformCmpMapper.get(entity)

        if (!currentBoundary.contains(transformCmp.interpolatedX, transformCmp.interpolatedY)) {
            // find new boundary
            // default is map boundary
            currentBoundary.set(mapBoundary)
            for (rect in camBoundaries) {
                if (rect.contains(transformCmp.interpolatedX, transformCmp.interpolatedY)) {
                    currentBoundary.set(rect)
                    break
                }
            }
        }

        val camW = camera.viewportWidth * 0.5f
        val camH = camera.viewportHeight * 0.5f
        camera.position.apply {
            x = MathUtils.clamp(transformCmp.interpolatedX, currentBoundary.x + camW, currentBoundary.x + currentBoundary.width - camW)
            y = MathUtils.clamp(transformCmp.interpolatedY, currentBoundary.y + camH, currentBoundary.y + currentBoundary.height - camH)
        }
    }

    override fun mapChanged(event: MapEvent) {
        currentBoundary.set(0f, 0f, 0f, 0f)
        mapBoundary.set(0f, 0f, event.width, event.height)
        mapManager.getCameraBoundaries(camBoundaries)
    }
}