package com.quillraven.masamune.physic

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.*
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.model.ECharacterType

class Q2DContactListener constructor(game: MainGame) : ContactListener {
    private val charCmpMapper = game.cmpMapper.character
    private var player: Entity? = null
    private var character: Entity? = null
    private val gameEventManager = game.gameEventManager

    private fun setCollisionEntities(fixA: Fixture, fixB: Fixture) {
        player = null
        character = null
        var playerSensor = false

        val dataA = fixA.body.userData
        if (dataA is Entity) {
            val charCmp = charCmpMapper.get(dataA)
            if (charCmp != null) {
                if (charCmp.type == ECharacterType.HERO) {
                    player = dataA
                    playerSensor = fixA.isSensor
                } else {
                    character = dataA
                }
            }
        }

        val dataB = fixB.body.userData
        if (dataB is Entity) {
            val charCmp = charCmpMapper.get(dataB)
            if (charCmp != null) {
                if (charCmp.type == ECharacterType.HERO) {
                    player = dataB
                    playerSensor = fixB.isSensor
                } else {
                    character = dataB
                }
            }
        }

        if (character != null && player != null && !playerSensor) {
            // char <-> player contact should only happen with player detection radius and not with its real collision body
            player = null
        }
    }

    override fun beginContact(contact: Contact) {
        setCollisionEntities(contact.fixtureA, contact.fixtureB)

        val tmpPlayer = player ?: return // no collision with player
        val tmpChar = character
        if (tmpChar != null) {
            // player <-> character collision
            gameEventManager.dispatchContactPlayerCharacterEvent(tmpPlayer, tmpChar, false)
        }
    }

    override fun endContact(contact: Contact) {
        setCollisionEntities(contact.fixtureA, contact.fixtureB)

        val tmpPlayer = player ?: return // no collision with player
        val tmpChar = character
        if (tmpChar != null) {
            // player <-> character collision
            gameEventManager.dispatchContactPlayerCharacterEvent(tmpPlayer, tmpChar, true)
        }
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {

    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {

    }
}