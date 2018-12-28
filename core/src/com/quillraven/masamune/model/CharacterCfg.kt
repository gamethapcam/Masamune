package com.quillraven.masamune.model

import com.badlogic.gdx.physics.box2d.BodyDef

data class CharacterCfg(
        val type: ECharacterType,
        val bodyType: BodyDef.BodyType,
        val texture: String,
        val width: Float,
        val height: Float,
        val speed: Float,
        val flip: Boolean
)