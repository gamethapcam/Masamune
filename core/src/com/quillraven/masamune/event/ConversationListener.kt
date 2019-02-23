package com.quillraven.masamune.event

import com.badlogic.ashley.core.Entity
import com.quillraven.masamune.model.Conversation
import com.quillraven.masamune.model.ConversationNode

interface ConversationListener {
    fun startConversation(conversation: Conversation)

    fun endConversation()

    fun updateConversation(node: ConversationNode)

    fun openShopConversation(conversationEntity: Entity, player: Entity)
}