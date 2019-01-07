package com.quillraven.masamune.physic

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.*
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.ecs.EntityType
import com.quillraven.masamune.model.ObjectType

class Q2DContactListener constructor(game: MainGame) : ContactListener {
    private val idCmpMapper = game.cmpMapper.identify
    private var player: Entity? = null
    private var character: Entity? = null
    private var item: Entity? = null
    private val gameEventManager = game.gameEventManager

    private fun setCollisionEntities(fixA: Fixture, fixB: Fixture) {
        player = null
        character = null
        item = null
        var playerSensor = false

        val dataA = fixA.body.userData
        if (dataA is Entity) {
            val idCmp = idCmpMapper.get(dataA)
            if (idCmp.entityType == EntityType.CHARACTER) {
                if (idCmp.type == ObjectType.HERO) {
                    player = dataA
                    playerSensor = fixA.isSensor
                } else {
                    character = dataA
                }
            } else if (idCmp.entityType == EntityType.ITEM) {
                item = dataA
            }
        }

        val dataB = fixB.body.userData
        if (dataB is Entity) {
            val idCmp = idCmpMapper.get(dataB)
            if (idCmp.entityType == EntityType.CHARACTER) {
                if (idCmp.type == ObjectType.HERO) {
                    player = dataB
                    playerSensor = fixB.isSensor
                } else {
                    character = dataB
                }
            } else if (idCmp.entityType == EntityType.ITEM) {
                item = dataB
            }
        }

        if ((character != null || item != null) && player != null && !playerSensor) {
            // char/item <-> player contact should only happen with player detection radius and not with its real collision body
            player = null
        }
    }

    override fun beginContact(contact: Contact) {
        setCollisionEntities(contact.fixtureA, contact.fixtureB)

        val tmpPlayer = player ?: return // no collision with player
        val tmpChar = character
        val tmpItem = item
        if (tmpChar != null) {
            // player <-> character collision
            gameEventManager.dispatchContactBeginCharacter(tmpPlayer, tmpChar)
        } else if (tmpItem != null) {
            gameEventManager.dispatchContactBeginItem(tmpPlayer, tmpItem)
        }
    }

    override fun endContact(contact: Contact) {
        setCollisionEntities(contact.fixtureA, contact.fixtureB)

        val tmpPlayer = player ?: return // no collision with player
        val tmpChar = character
        val tmpItem = item
        if (tmpChar != null) {
            // player <-> character collision
            gameEventManager.dispatchContactEndCharacter(tmpPlayer, tmpChar)
        } else if (tmpItem != null) {
            gameEventManager.dispatchContactEndItem(tmpPlayer, tmpItem)
        }
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {

    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {

    }
}