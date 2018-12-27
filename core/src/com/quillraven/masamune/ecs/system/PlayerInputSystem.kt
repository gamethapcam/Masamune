package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import com.badlogic.ashley.systems.IteratingSystem
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.CmpMapperB2D
import com.quillraven.masamune.ecs.CmpMapperMove
import com.quillraven.masamune.ecs.component.MoveComponent
import com.quillraven.masamune.ecs.component.PlayerInputComponent
import com.quillraven.masamune.event.EInputType
import com.quillraven.masamune.event.InputEvent

class PlayerInputSystem constructor(game: MainGame) : IteratingSystem(Family.all(PlayerInputComponent::class.java, MoveComponent::class.java).get()), Listener<InputEvent> {
    private var percX = 0f
    private var percY = 0f

    init {
        game.gameEventManager.addInputEventListener(this)
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val b2dCmp = CmpMapperB2D.get(entity)
        val moveCmp = CmpMapperMove.get(entity)
        b2dCmp.body.apply {
            applyLinearImpulse((moveCmp.speed * percX - linearVelocity.x) * mass, (moveCmp.speed * percY - linearVelocity.y) * mass, worldCenter.x, worldCenter.y, true)
        }
    }

    override fun receive(signal: Signal<InputEvent>?, obj: InputEvent) {
        if (obj.type == EInputType.MOVE) {
            percX = obj.movePercX
            percY = obj.movePercY
        }
    }
}