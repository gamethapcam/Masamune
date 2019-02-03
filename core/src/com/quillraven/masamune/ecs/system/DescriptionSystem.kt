package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.*
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.component.DescriptionComponent
import com.quillraven.masamune.ecs.component.IdentifyComponent

class DescriptionSystem constructor(game: MainGame) : EntitySystem(), EntityListener {
    private val idMapper = game.cmpMapper.identify
    private val descMapper = game.cmpMapper.description
    private val i18nBundle = game.resourceBundle

    init {
        setProcessing(false)
    }

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(Family.all(DescriptionComponent::class.java, IdentifyComponent::class.java).get(), this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
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