package com.quillraven.masamune.event

import com.badlogic.ashley.core.Entity

interface ContactListener {
    fun beginCharacterContact(player: Entity, character: Entity)

    fun endCharacterContact(player: Entity, character: Entity)
}