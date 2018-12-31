package com.quillraven.masamune

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.*
import com.quillraven.masamune.model.ECharacterType

class Q2DContactListener constructor(game: MainGame) : ContactListener {
    private val charCmpMapper = game.cmpMapper.character
    private var player: Entity? = null
    private var character: Entity? = null
    private val gameEventManager = game.gameEventManager

    private fun setCollisionEntities(bodyA: Body, bodyB: Body) {
        player = null
        character = null

        val dataA = bodyA.userData
        if (dataA is Entity) {
            val charCmp = charCmpMapper.get(dataA)
            if (charCmp != null) {
                if (charCmp.type == ECharacterType.HERO) {
                    player = dataA
                } else {
                    character = dataA
                }
            }
        }

        val dataB = bodyB.userData
        if (dataB is Entity) {
            val charCmp = charCmpMapper.get(dataB)
            if (charCmp != null) {
                if (charCmp.type == ECharacterType.HERO) {
                    player = dataB
                } else {
                    character = dataB
                }
            }
        }
    }

    override fun beginContact(contact: Contact) {
        setCollisionEntities(contact.fixtureA.body, contact.fixtureB.body)

        val tmpPlayer = player
        if (tmpPlayer == null) {
            // no collision with player
            return
        }

        val tmpChar = character
        if (tmpChar != null) {
            // player <-> character collision
            gameEventManager.dispatchContactPlayerCharacterEvent(tmpPlayer, tmpChar)
        }
    }

    override fun endContact(contact: Contact) {

    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {

    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {

    }
}