package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.*
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.component.AttributeComponent
import com.quillraven.masamune.event.ItemListener
import com.quillraven.masamune.model.EAttributeType
import com.quillraven.masamune.model.EEquipType

private const val TAG = "AttributeSystem"

class AttributeSystem constructor(game: MainGame) : EntitySystem(), EntityListener, ItemListener {
    private val attributeCmpMapper = game.cmpMapper.attribute

    init {
        game.gameEventManager.addItemListener(this)
        setProcessing(false)
    }

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(Family.all(AttributeComponent::class.java).get(), this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }

    override fun entityAdded(entity: Entity) {
        val attrCmp = attributeCmpMapper.get(entity)
        while (attrCmp.attributes.size < EAttributeType.values().size) {
            attrCmp.attributes.add(0f)
        }
    }

    override fun entityRemoved(entity: Entity) {
    }

    override fun equipSlotUpdated(entity: Entity, type: EEquipType, prevItem: Entity?, newItem: Entity?) {
        val entityAttrCmp = attributeCmpMapper.get(entity)
        if (prevItem != null) {
            // unequip -> update attributes
            val itemAttrCmp = attributeCmpMapper.get(prevItem)
            if (itemAttrCmp != null && entityAttrCmp != null) {
                for (attType in EAttributeType.values()) {
                    entityAttrCmp.attributes[attType.ordinal] -= itemAttrCmp.attributes[attType.ordinal]
                }
            }
        }
        if (newItem != null) {
            // new equipment -> update attributes
            val itemAttrCmp = attributeCmpMapper.get(newItem)
            if (itemAttrCmp != null && entityAttrCmp != null) {
                for (attType in EAttributeType.values()) {
                    entityAttrCmp.attributes[attType.ordinal] += itemAttrCmp.attributes[attType.ordinal]
                }
            }
        }

        debugEntityAttributes(entityAttrCmp)
    }

    private fun debugEntityAttributes(entityAttrCmp: AttributeComponent?) {
        if (Gdx.app.logLevel == Application.LOG_DEBUG && entityAttrCmp != null) {
            Gdx.app.debug(TAG, "Attributes updated")
            for (attType in EAttributeType.values()) {
                Gdx.app.debug(TAG, "$attType : ${entityAttrCmp.attributes[attType.ordinal]}")
            }
        }
    }

    override fun useItem(entity: Entity, item: Entity) {
        val itemAttrCmp = attributeCmpMapper.get(item)
        val entityAttrCmp = attributeCmpMapper.get(entity)
        if (entityAttrCmp != null && itemAttrCmp != null) {
            Gdx.app.debug(TAG, "Using attribute item")
            for (attType in EAttributeType.values()) {
                entityAttrCmp.attributes[attType.ordinal] += itemAttrCmp.attributes[attType.ordinal]
            }
            debugEntityAttributes(entityAttrCmp)
        }
    }
}