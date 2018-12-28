package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.component.RenderComponent
import com.quillraven.masamune.ecs.component.RenderFlipComponent

class RenderFlipSystem constructor(game: MainGame) : IteratingSystem(Family.all(RenderComponent::class.java, RenderFlipComponent::class.java).get()) {
    private val flipCmpMapper = game.cmpMapper.renderFlip
    private val renderCmpMapper = game.cmpMapper.render

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val flipCmp = flipCmpMapper.get(entity)
        flipCmp.flipCountdown += deltaTime
        if (flipCmp.flipCountdown > 1f) {
            val renderCmp = renderCmpMapper.get(entity)
            renderCmp.flipX = !renderCmp.flipX
            flipCmp.flipCountdown = 0f
        }
    }
}