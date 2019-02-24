package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.*
import com.badlogic.gdx.Gdx
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.component.ExperienceComponent
import com.quillraven.masamune.event.ItemListener

private const val TAG = "ExperienceSystem"

class ExperienceSystem constructor(game: MainGame) : EntitySystem(), EntityListener, ItemListener {
    private val xpCmpMapper = game.cmpMapper.experience
    private val eventMgr = game.gameEventManager

    init {
        setProcessing(false)
        eventMgr.addItemListener(this)
    }

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(Family.all(ExperienceComponent::class.java).get(), this)
    }

    override fun entityAdded(entity: Entity) {
        val xpCmp = xpCmpMapper.get(entity)
        if (xpCmp.level == 0) {
            // initialize experience
            xpCmp.level = 1
            xpCmp.xpNeeded = getRequiredXP(xpCmp.level)
        }
        eventMgr.dispatchExperienceLevelUp(entity, xpCmp.level)
        eventMgr.dispatchExperienceUpdated(entity, xpCmp.xp, xpCmp.xpNeeded)
        eventMgr.dispatchExperienceSkillPointsUpdated(entity, xpCmp.unspentSkillPoints)
    }

    private fun getRequiredXP(level: Int): Int {
        return when (level) {
            1 -> 200
            2 -> 350
            3 -> 600
            4 -> 1100
            5 -> 2000
            6 -> 3000
            7 -> 4500
            8 -> 7000
            9 -> 10000
            else -> 15000
        }
    }

    private fun getSkillPoints(level: Int): Int {
        return when (level) {
            1, 2 -> 0
            in 3..5 -> 1
            6 -> 0
            7 -> 2
            8, 9 -> 0
            else -> 3
        }
    }

    override fun entityRemoved(entity: Entity) {
    }

    override fun useItem(entity: Entity, item: Entity) {
        val itemXpCmp = xpCmpMapper.get(item)

        if (itemXpCmp != null) {
            increaseXP(entity, itemXpCmp.xp)
        }
    }

    private fun increaseXP(entity: Entity, xp: Int) {
        val entityXpCmp = xpCmpMapper.get(entity)
        if (entityXpCmp == null) {
            Gdx.app.error(TAG, "Trying to increase experience of an entity without experience component")
            return
        }

        entityXpCmp.xp += xp
        // maximum level is 10
        while (entityXpCmp.xp >= entityXpCmp.xpNeeded && entityXpCmp.level < 10) {
            ++entityXpCmp.level
            entityXpCmp.unspentSkillPoints += getSkillPoints(entityXpCmp.level)
            eventMgr.dispatchExperienceSkillPointsUpdated(entity, entityXpCmp.unspentSkillPoints)
            entityXpCmp.xpNeeded = getRequiredXP(entityXpCmp.level)
            eventMgr.dispatchExperienceLevelUp(entity, entityXpCmp.level)
        }
        eventMgr.dispatchExperienceUpdated(entity, entityXpCmp.xp, entityXpCmp.xpNeeded)
    }
}