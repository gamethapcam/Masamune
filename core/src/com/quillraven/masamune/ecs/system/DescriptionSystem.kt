package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.ecs.component.DescriptionComponent
import com.quillraven.masamune.ecs.component.IdentifyComponent

class DescriptionSystem constructor(game: MainGame, ecsEngine: ECSEngine) : EntitySystem(), EntityListener {
    private val idMapper = game.cmpMapper.identify
    private val descMapper = game.cmpMapper.description
    private val i18nBundle = game.resourceBundle

    init {
        ecsEngine.addEntityListener(Family.all(DescriptionComponent::class.java, IdentifyComponent::class.java).get(), this)
        setProcessing(false)
    }

    override fun entityAdded(entity: Entity) {
        val idCmp = idMapper.get(entity)
        val descCmp = descMapper.get(entity)

        descCmp.name = i18nBundle.get("${idCmp.type}.name")
        descCmp.description = i18nBundle.get("${idCmp.type}.description")
    }

    override fun entityRemoved(entity: Entity) {
    }
}