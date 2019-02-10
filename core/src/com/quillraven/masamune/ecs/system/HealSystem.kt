package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.event.ItemListener
import com.quillraven.masamune.model.EAttributeType

private const val TAG = "HealSystem"

class HealSystem constructor(game: MainGame) : EntitySystem(), ItemListener {
    private val healCmpMapper = game.cmpMapper.heal
    private val attrCmpMapper = game.cmpMapper.attribute
    private val gameEventMgr = game.gameEventManager

    init {
        game.gameEventManager.addItemListener(this)
        setProcessing(false)
    }

    override fun useItem(entity: Entity, item: Entity) {
        val itemHealCmp = healCmpMapper.get(item)
        val entityAttrCmp = attrCmpMapper.get(entity)
        if (entityAttrCmp != null && itemHealCmp != null) {
            Gdx.app.debug(TAG, "Using healing item for ${itemHealCmp.life} life and ${itemHealCmp.mana} mana")
            entityAttrCmp.attributes.set(EAttributeType.LIFE.ordinal, Math.min(entityAttrCmp.attributes[EAttributeType.MAX_LIFE.ordinal], entityAttrCmp.attributes[EAttributeType.LIFE.ordinal] + itemHealCmp.life))
            entityAttrCmp.attributes.set(EAttributeType.MANA.ordinal, Math.min(entityAttrCmp.attributes[EAttributeType.MAX_MANA.ordinal], entityAttrCmp.attributes[EAttributeType.MANA.ordinal] + itemHealCmp.mana))
            gameEventMgr.dispatchAttributeUpdated(entity, EAttributeType.LIFE, entityAttrCmp.attributes[EAttributeType.LIFE.ordinal])
            gameEventMgr.dispatchAttributeUpdated(entity, EAttributeType.MAX_LIFE, entityAttrCmp.attributes[EAttributeType.MAX_LIFE.ordinal])
            gameEventMgr.dispatchAttributeUpdated(entity, EAttributeType.MANA, entityAttrCmp.attributes[EAttributeType.MANA.ordinal])
            gameEventMgr.dispatchAttributeUpdated(entity, EAttributeType.MAX_MANA, entityAttrCmp.attributes[EAttributeType.MAX_MANA.ordinal])
        }
    }
}