package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Box2D
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.ecs.component.Box2DComponent
import com.quillraven.masamune.ecs.component.TransformComponent

class Box2DSystem constructor(game: MainGame, ecsEngine: ECSEngine, private val fixedStep: Float = 1 / 60f) : EntitySystem() {
    private val b2dCmpMapper = game.cmpMapper.box2D
    private val transformCmpMapper = game.cmpMapper.transform
    private var accumulator = 0f
    private val world = game.world
    private val b2dEntities = ecsEngine.getEntitiesFor(Family.all(Box2DComponent::class.java, TransformComponent::class.java).get())

    init {
        Box2D.init()
    }

    override fun update(deltaTime: Float) {
        accumulator += deltaTime

        while (accumulator >= fixedStep) {
            for (entity in b2dEntities) {
                val b2dCmp = b2dCmpMapper.get(entity)
                val transformCmp = transformCmpMapper.get(entity)
                transformCmp.prevX = b2dCmp.body.position.x
                transformCmp.prevY = b2dCmp.body.position.y
                transformCmp.prevAngle = b2dCmp.body.angle
            }
            accumulator -= fixedStep
            world.step(fixedStep, 6, 2)
        }

        val alpha = accumulator / fixedStep
        for (entity in b2dEntities) {
            val b2dCmp = b2dCmpMapper.get(entity)
            val transformCmp = transformCmpMapper.get(entity)

            // save real values for save/load logic
            transformCmp.x = b2dCmp.body.position.x - transformCmp.width * 0.5f
            transformCmp.y = b2dCmp.body.position.y - transformCmp.height * 0.5f
            transformCmp.angle = b2dCmp.body.angle

            // calculate interpolated values for rendering
            transformCmp.interpolatedX = MathUtils.lerp(transformCmp.prevX, b2dCmp.body.position.x, alpha)
            transformCmp.interpolatedY = MathUtils.lerp(transformCmp.prevY, b2dCmp.body.position.y, alpha)
            transformCmp.interpolatedAngle = MathUtils.lerpAngle(transformCmp.prevAngle, b2dCmp.body.angle, alpha)
        }
    }
}