package com.quillraven.masamune.ecs.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.MathUtils
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.ecs.components.Box2DComponent

private const val FIXED_STEP = 1 / 60f

class Box2DSystem constructor(game: MainGame) : EntitySystem() {
    private var accumulator = 0f
    private val world = game.world
    private val b2dEntities by lazy { engine.getEntitiesFor(Family.all(Box2DComponent::class.java).get()) }

    override fun update(deltaTime: Float) {
        var setPrevPosition = true
        accumulator += deltaTime

        while (accumulator >= FIXED_STEP) {
            if (setPrevPosition) {
                setPrevPosition = false
                for (entity in b2dEntities) {
                    val b2dCmp = (engine as ECSEngine).box2DMapper.get(entity)
                    b2dCmp.prevX = b2dCmp.body!!.position.x
                    b2dCmp.prevY = b2dCmp.body!!.position.y
                }
            }
            accumulator -= FIXED_STEP
            world.step(FIXED_STEP, 6, 2)
        }

        val alpha = accumulator / FIXED_STEP
        for (entity in b2dEntities) {
            val b2dCmp = (engine as ECSEngine).box2DMapper.get(entity)
            b2dCmp.interpolatedX = MathUtils.lerp(b2dCmp.prevX, b2dCmp.body!!.position.x, alpha)
            b2dCmp.interpolatedY = MathUtils.lerp(b2dCmp.prevY, b2dCmp.body!!.position.y, alpha)
        }
    }
}