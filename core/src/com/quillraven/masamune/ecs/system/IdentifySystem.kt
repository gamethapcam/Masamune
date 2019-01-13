package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.*
import com.badlogic.gdx.utils.Array
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.ecs.EntityType
import com.quillraven.masamune.ecs.component.IdentifyComponent
import com.quillraven.masamune.model.ObjectType

private const val TAG = "IdentifySystem"
const val DEFAULT_ENTITY_ID = 0

class IdentifySystem constructor(game: MainGame, ecsEngine: ECSEngine) : EntitySystem(), EntityListener, Disposable, Json.Serializer<IdentifySystem> {
    private val entityMapById = IntMap<Entity>()
    private val entityMapByType = ObjectMap<EntityType, Array<Entity>>()
    private val immutableEntityMapByType = ObjectMap<EntityType, ImmutableArray<Entity>>()
    private val idCmpMapper = game.cmpMapper.identify
    private var currentIdIdx = DEFAULT_ENTITY_ID
    private lateinit var playerEntity: Entity

    init {
        ecsEngine.addEntityListener(Family.all(IdentifyComponent::class.java).get(), this)
        for (type in EntityType.values()) {
            if (type == EntityType.UNDEFINED) continue

            val entities = Array<Entity>()
            entityMapByType.put(type, entities)
            immutableEntityMapByType.put(type, ImmutableArray<Entity>(entities))
        }

        setProcessing(false)
    }

    override fun entityAdded(entity: Entity) {
        val idCmp = idCmpMapper.get(entity)
        if (idCmp.id == DEFAULT_ENTITY_ID) {
            ++currentIdIdx
            // assign new ID
            idCmp.id = currentIdIdx
            Gdx.app.debug(TAG, "Assigning id ${idCmp.id} to entity of entityType ${idCmp.entityType} and type ${idCmp.type}")
        }

        if (idCmp.type == ObjectType.HERO) {
            // player ID
            playerEntity = entity
        }

        // add to entity maps for fast access
        Gdx.app.debug(TAG, "Adding entity with id ${idCmp.id} and type ${idCmp.type}")
        entityMapById.put(idCmp.id, entity)
        entityMapByType.get(idCmp.entityType).add(entity)
    }

    override fun entityRemoved(entity: Entity) {
        val idCmp = idCmpMapper.get(entity)
        entityMapById.remove(idCmp.id)
        entityMapByType.get(idCmp.entityType).removeValue(entity, true)
        if (entity == playerEntity) {
            Gdx.app.debug(TAG, "Player got removed")
        }
    }

    fun getPlayerEntity(): Entity {
        return playerEntity
    }

    fun getEntitiesOfType(entityType: EntityType): ImmutableArray<Entity> {
        return immutableEntityMapByType.get(entityType)
    }

    fun getEntityByID(id: Int): Entity? {
        val entity = entityMapById.get(id)
        if (entity == null) {
            Gdx.app.error(TAG, "Trying to access invalid entity by id $id")
        }
        return entity
    }

    override fun dispose() {
        Gdx.app.debug(TAG, "number of entities ${engine.entities.size()}")
        Gdx.app.debug(TAG, "number of entities with id: ${entityMapById.size}")

        for (type in EntityType.values()) {
            if (type == EntityType.UNDEFINED) continue

            Gdx.app.debug(TAG, "number of entities of type $type: ${entityMapByType.get(type).size}")
        }
    }

    override fun write(json: Json, obj: IdentifySystem, knownType: Class<*>?) {
        json.writeObjectStart()
        json.writeValue("currentIdx", currentIdIdx)
        json.writeObjectEnd()
    }

    override fun read(json: Json, jsonData: JsonValue, type: Class<*>?): IdentifySystem {
        currentIdIdx = jsonData.getInt("currentIdx")
        return this
    }
}