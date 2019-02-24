package com.quillraven.masamune.event

import com.badlogic.ashley.core.Entity

interface ExperienceListener {
    fun experienceUpdated(entity: Entity, newXP: Int, requiredXP: Int)

    fun skillPointsUpdated(entity: Entity, unspentSkillPoints: Int)

    fun levelUp(entity: Entity, newLevel: Int)
}