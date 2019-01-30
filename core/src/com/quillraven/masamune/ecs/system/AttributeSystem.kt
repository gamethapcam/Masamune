package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.ecs.component.AttributeComponent
import com.quillraven.masamune.model.EAttributeType

class AttributeSystem constructor(game: MainGame, ecsEngine: ECSEngine) : EntitySystem(), EntityListener {
    private val attributeCmpMapper = game.cmpMapper.attribute

    init {
        ecsEngine.addEntityListener(Family.all(AttributeComponent::class.java).get(), this)
        setProcessing(false)
    }

    override fun entityAdded(entity: Entity) {
        val attrCmp = attributeCmpMapper.get(entity)
        while (attrCmp.attributes.size < EAttributeType.values().size) {
            attrCmp.attributes.add(0f)
        }
    }

    override fun entityRemoved(entity: Entity) {
    }
}