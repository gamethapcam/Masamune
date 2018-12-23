package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.ecs.component.PlayerInputComponent

class PlayerInputSystem : IteratingSystem(Family.all(PlayerInputComponent::class.java).get()) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val b2dCmp = (engine as ECSEngine).box2DMapper.get(entity)
        val body = b2dCmp.body

        when {
            Gdx.input.isKeyPressed(Input.Keys.W) -> body?.applyLinearImpulse((0 - body.linearVelocity.x) * body.mass, (5 - body.linearVelocity.y) * body.mass, body.worldCenter.x, body.worldCenter.y, true)
            Gdx.input.isKeyPressed(Input.Keys.S) -> body?.applyLinearImpulse((0 - body.linearVelocity.x) * body.mass, (-5 - body.linearVelocity.y) * body.mass, body.worldCenter.x, body.worldCenter.y, true)
            Gdx.input.isKeyPressed(Input.Keys.A) -> body?.applyLinearImpulse((-5 - body.linearVelocity.x) * body.mass, (0 - body.linearVelocity.y) * body.mass, body.worldCenter.x, body.worldCenter.y, true)
            Gdx.input.isKeyPressed(Input.Keys.D) -> body?.applyLinearImpulse((5 - body.linearVelocity.x) * body.mass, (0 - body.linearVelocity.y) * body.mass, body.worldCenter.x, body.worldCenter.y, true)
        }
    }
}