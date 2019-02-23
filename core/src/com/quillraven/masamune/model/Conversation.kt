package com.quillraven.masamune.model

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.IntMap

class Conversation constructor(internal val id: String, startNode: ConversationNode) {
    private val nodes = IntMap<ConversationNode>()
    private val startNodeId: Int = startNode.id

    init {
        addNode(startNode)
    }

    fun addNode(node: ConversationNode) {
        // validate node
        if (node.links.isEmpty) throw GdxRuntimeException("Error for conversation $id. Trying to add a conversation node with no links $node")
        if (node.narratorKey.isBlank() || node.txtKey.isBlank()) throw GdxRuntimeException("Error for conversation $id. Trying to add an invalid conversation node. Check narratorKey and txtKey for node ${node.id}")
        if (nodes.containsKey(node.id)) throw GdxRuntimeException("Error for conversation $id. Duplicated conversation node detected with id ${node.id}")
        for (link in node.links) {
            if (link.txtKey.isBlank()) throw GdxRuntimeException("Error for conversation $id. Node $node contains a link with empty text")
        }

        // node is valid --> add it to map
        nodes.put(node.id, node)
    }

    // validate should be called once the conversation is fully created to check if it has any invalid links
    fun validate() {
        for (node in nodes.values()) {
            for (link in node.links) {
                if (link.type == ConversationLinkType.NODE && !nodes.containsKey(link.targetNodeId)) {
                    throw GdxRuntimeException("Error for conversation $id. Link ${link.txtKey} of node ${node.id} is linking to a non-existing node ${link.targetNodeId}")
                } else if (link.type != ConversationLinkType.NODE && nodes.containsKey(link.targetNodeId)) {
                    throw GdxRuntimeException("Error for conversation $id. Link ${link.txtKey} of node ${node.id} is linking to a node but its type is not of type NODE")
                }
            }
        }
    }

    fun getStartNode(): ConversationNode {
        return nodes.get(startNodeId)
    }
}

class ConversationNode constructor(internal val id: Int, internal val narratorKey: String, internal val txtKey: String, internal val imgKey: String) {
    internal val links = Array<ConversationLink>()
}

class ConversationLink constructor(internal val txtKey: String, internal val targetNodeId: Int = 0, internal val type: ConversationLinkType = ConversationLinkType.NODE)

enum class ConversationLinkType {
    NODE, OPEN_SHOP, EXIT
}