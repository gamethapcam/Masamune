package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.component.MoveComponent
import com.quillraven.masamune.ecs.component.PlayerInputComponent
import com.quillraven.masamune.event.InputListener

class PlayerInputSystem constructor(game: MainGame) : IteratingSystem(Family.all(PlayerInputComponent::class.java, MoveComponent::class.java).get()), InputListener {
    private val b2dCmpMapper = game.cmpMapper.box2D
    private val moveCmpMapper = game.cmpMapper.move

    private var percX = 0f
    private var percY = 0f

    init {
        game.gameEventManager.addInputListener(this)
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val b2dCmp = b2dCmpMapper.get(entity)
        val moveCmp = moveCmpMapper.get(entity)
        b2dCmp.body.apply {
            applyLinearImpulse((moveCmp.speed * percX - linearVelocity.x) * mass, (moveCmp.speed * percY - linearVelocity.y) * mass, worldCenter.x, worldCenter.y, true)
        }
    }

    override fun inputMove(percentX: Float, percentY: Float) {
        percX = percentX
        percY = percentY
    }
}