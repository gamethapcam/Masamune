package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Camera
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.ecs.component.CameraComponent

class CameraSystem constructor(game: MainGame, private val camera: Camera = game.gameViewPort.camera) : IteratingSystem(Family.all(CameraComponent::class.java).get()) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val b2dCmp = (engine as ECSEngine).box2DMapper.get(entity)
        camera.position.set(b2dCmp.interpolatedX, b2dCmp.interpolatedY, 0f)
    }
}