package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class ExperienceComponent : Pool.Poolable, Component {
    var xp = 0
    var xpNeeded = 0
    var level = 0
    var unspentSkillPoints = 0

    override fun reset() {
        xp = 0
        xpNeeded = 0
        level = 0
        unspentSkillPoints = 0
    }
}