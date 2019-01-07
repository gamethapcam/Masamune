package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Array
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.EntityType
import com.quillraven.masamune.ecs.component.IdentifyComponent
import com.quillraven.masamune.model.ObjectType

private const val TAG = "IdentifySystem"

class IdentifySystem constructor(game: MainGame) : IteratingSystem(Family.all(IdentifyComponent::class.java).get()) {
    private val idCmpMapper = game.cmpMapper.identify
    private var currentIdIdx = -1
    private var playerEntity: Entity? = null

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val idCmp = idCmpMapper.get(entity)
        if (idCmp.id == -1) {
            ++currentIdIdx
            if (idCmp.type == ObjectType.HERO) {
                // player ID
                if (playerEntity != null) {
                    Gdx.app.error(TAG, "Created another player entity")
                }
                playerEntity = entity
            }

            // assign new ID
            idCmp.id = currentIdIdx
        }
    }

    fun getPlayerEntity(): Entity? {
        if (playerEntity == null) {
            Gdx.app.error(TAG, "Trying to get non-existing player entity")
        }
        return playerEntity
    }

    fun getEntitiesOfType(entityType: EntityType, fill: Array<Entity>) {
        fill.clear()
        for (entity in entities) {
            if (idCmpMapper.get(entity).entityType == entityType) {
                fill.add(entity)
            }
        }
    }

    fun getEntityByID(id: Int): Entity? {
        for (entity in entities) {
            if (idCmpMapper.get(entity).id == id) {
                return entity
            }
        }

        Gdx.app.error(TAG, "Trying to access invalid entity by id $id")
        return null
    }
}