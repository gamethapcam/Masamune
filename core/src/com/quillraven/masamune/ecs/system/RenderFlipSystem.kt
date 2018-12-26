package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.quillraven.masamune.ecs.CmpMapperFlip
import com.quillraven.masamune.ecs.CmpMapperRender
import com.quillraven.masamune.ecs.component.RenderComponent
import com.quillraven.masamune.ecs.component.RenderFlipComponent

class RenderFlipSystem : IteratingSystem(Family.all(RenderComponent::class.java, RenderFlipComponent::class.java).get()) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val flipCmp = CmpMapperFlip.get(entity)
        flipCmp.flipCountdown += deltaTime
        if (flipCmp.flipCountdown > 1f) {
            val renderCmp = CmpMapperRender.get(entity)
            renderCmp.flipX = !renderCmp.flipX
            flipCmp.flipCountdown = 0f
        }
    }
}