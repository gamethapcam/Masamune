package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.event.InputListener
import com.quillraven.masamune.model.Conversation
import com.quillraven.masamune.model.ConversationCache
import com.quillraven.masamune.model.ConversationLinkType
import com.quillraven.masamune.model.ConversationNode

private const val TAG = "ConversationSystem"

class ConversationSystem constructor(game: MainGame) : EntitySystem(), InputListener {
    private val convCmpMapper = game.cmpMapper.conversation
    private val idCmpMapper = game.cmpMapper.identify
    private val conversationCache = game.assetManager.get("conversation/allConversations.json", ConversationCache::class.java)
    private val gameEventMgr = game.gameEventManager

    private var conversationEntity: Entity? = null
    private var currentConversation: Conversation? = null
    private var currentNode: ConversationNode? = null

    init {
        game.gameEventManager.addInputListener(this)
        setProcessing(false)
    }

    fun startConversation(entity: Entity) {
        val convCmp = convCmpMapper.get(entity)
        val conversation = conversationCache[convCmp.currentConversationId]

        if (conversation == null) {
            val idCmp = idCmpMapper.get(entity)
            Gdx.app.error(TAG, "Trying to start a conversation with entity ${idCmp.type}-${idCmp.id} but it has no valid conversation id: ${convCmp.currentConversationId})")
            return
        }

        if (currentConversation != null) {
            // stop previous conversation
            endConversation()
        }

        conversationEntity = entity
        currentConversation = conversation
        currentNode = conversation.getStartNode()
        gameEventMgr.dispatchConversationStart(conversation)
    }

    private fun endConversation() {
        currentConversation = null
        currentNode = null
        conversationEntity = null
        gameEventMgr.dispatchConversationEnd()
    }

    override fun inputConversationLink(linkIdx: Int) {
        val conversation = currentConversation
        if (conversation == null) {
            Gdx.app.error(TAG, "Trying to process link $linkIdx of a null conversation")
            return
        }

        val convEntity = conversationEntity
        if (convEntity == null) {
            Gdx.app.error(TAG, "Trying to process conversation ${conversation.id} of a null entity")
            return
        }

        val node = currentNode
        if (node == null) {
            Gdx.app.error(TAG, "Trying to process link $linkIdx of a null node for conversation ${conversation.id}")
            return
        } else if (linkIdx >= node.links.size || linkIdx < 0) {
            Gdx.app.error(TAG, "Trying to process an invalid link $linkIdx of node ${node.id} of conversation ${conversation.id}")
            return
        }

        val link = node.links[linkIdx]
        Gdx.app.debug(TAG, "Processing link ${link.txtKey} for node ${node.id} and conversation ${conversation.id}")
        when (link.type) {
            ConversationLinkType.EXIT -> endConversation()
            ConversationLinkType.NODE -> {
                val nextNode = conversation.getNode(link.targetNodeId)
                currentNode = nextNode
                gameEventMgr.dispatchConversationUpdated(nextNode)
            }
            ConversationLinkType.OPEN_SHOP -> {
                endConversation()
                gameEventMgr.dispatchConversationOpenShop(convEntity, engine.getSystem(IdentifySystem::class.java).getPlayerEntity())
            }
        }
    }
}