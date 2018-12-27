package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Box2D
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.CmpMapperB2D
import com.quillraven.masamune.ecs.component.Box2DComponent

private const val FIXED_STEP = 1 / 60f

class Box2DSystem constructor(game: MainGame) : EntitySystem() {
    private var accumulator = 0f
    private val world = game.world
    private val b2dEntities by lazy { engine.getEntitiesFor(Family.all(Box2DComponent::class.java).get()) }

    init {
        Box2D.init()
    }

    override fun update(deltaTime: Float) {
        accumulator += deltaTime

        while (accumulator >= FIXED_STEP) {
            for (entity in b2dEntities) {
                val b2dCmp = CmpMapperB2D.get(entity)
                b2dCmp.prevX = b2dCmp.body.position.x
                b2dCmp.prevY = b2dCmp.body.position.y
                b2dCmp.prevAngle = b2dCmp.body.angle
            }
            accumulator -= FIXED_STEP
            world.step(FIXED_STEP, 6, 2)
        }

        val alpha = accumulator / FIXED_STEP
        for (entity in b2dEntities) {
            val b2dCmp = CmpMapperB2D.get(entity)
            b2dCmp.interpolatedX = MathUtils.lerp(b2dCmp.prevX, b2dCmp.body.position.x, alpha)
            b2dCmp.interpolatedY = MathUtils.lerp(b2dCmp.prevY, b2dCmp.body.position.y, alpha)
            b2dCmp.interpolatedAngle = MathUtils.lerpAngle(b2dCmp.prevAngle, b2dCmp.body.angle, alpha)
        }
    }
}