package com.quillraven.masamune

import com.badlogic.gdx.physics.box2d.BodyDef

data class CharacterCfg(
        val name: String,
        val type: BodyDef.BodyType,
        val texture: String,
        val width: Float,
        val height: Float,
        val speed: Float,
        val flip: Boolean
)