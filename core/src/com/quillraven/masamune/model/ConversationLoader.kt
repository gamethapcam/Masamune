package com.quillraven.masamune.model

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.JsonReader

class ConversationLoader(resolver: FileHandleResolver) : AsynchronousAssetLoader<ConversationCache, ConversationLoader.ConversationLoaderParameter>(resolver) {
    private val convCache = ConversationCache()
    private val reader = JsonReader()

    class ConversationLoaderParameter : AssetLoaderParameters<ConversationCache>()

    override fun getDependencies(fileName: String?, file: FileHandle?, parameter: ConversationLoaderParameter?): Array<AssetDescriptor<Any>>? {
        return null
    }

    override fun loadSync(manager: AssetManager?, fileName: String?, file: FileHandle?, parameter: ConversationLoaderParameter?): ConversationCache {
        return convCache
    }

    override fun loadAsync(manager: AssetManager, fileName: String?, file: FileHandle, parameter: ConversationLoaderParameter?) {
        val jsonValue = reader.parse(file)
        var entry = jsonValue.child
        while (entry != null) {
            val conversation = loadConversation(entry.name, resolve("conversation/${entry.asString()}"))
            if (convCache.containsKey(conversation.id)) throw GdxRuntimeException("Trying to add conversation ${conversation.id} multiple times")
            convCache.put(conversation.id, conversation)
            entry = entry.next
        }
    }

    private fun loadConversation(conversationId: String, file: FileHandle): Conversation {
        if (!file.exists()) throw GdxRuntimeException("Missing conversation file ${file.path()}")
        val jsonValue = reader.parse(file)
                ?: throw GdxRuntimeException("There is no conversation defined in file ${file.path()}")

        var entry = jsonValue.child
        var conversation: Conversation? = null

        while (entry != null) {
            // create node
            val node = ConversationNode(entry.getInt("id"), entry.getString("narratorKey"), entry.getString("txtKey"), entry.getString("imgKey"))
            // create links of node
            val links = entry.get("links")
            var linkEntry = links.child
            while (linkEntry != null) {
                if (linkEntry.has("targetNodeId")) {
                    node.links.add(ConversationLink(linkEntry.getString("txtKey"), linkEntry.getInt("targetNodeId"), ConversationLinkType.valueOf(linkEntry.getString("type"))))
                } else {
                    node.links.add(ConversationLink(linkEntry.getString("txtKey"), type = ConversationLinkType.valueOf(linkEntry.getString("type"))))
                }
                linkEntry = linkEntry.next
            }

            if (conversation == null) {
                // create conversation if it was not created yet
                conversation = Conversation(conversationId, node)
            } else {
                // add node to existing conversation
                conversation.addNode(node)
            }

            entry = entry.next
        }
        // validate of conversation was correctly created
        conversation!!.validate()
        return conversation
    }
}