package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.quillraven.masamune.model.ECharacterType

class CharacterComponent : Component, Pool.Poolable {
    var type: ECharacterType = ECharacterType.UNDEFINED

    override fun reset() {
        type = ECharacterType.UNDEFINED
    }
}