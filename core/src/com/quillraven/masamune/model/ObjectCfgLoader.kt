package com.quillraven.masamune.model

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.JsonReader

class ObjectCfgLoader(resolver: FileHandleResolver) : AsynchronousAssetLoader<ObjectCfgMap, ObjectCfgLoader.ObjectCfgLoaderParameter>(resolver) {
    private val cfgMap = ObjectCfgMap()

    class ObjectCfgLoaderParameter : AssetLoaderParameters<ObjectCfgMap>()

    override fun getDependencies(fileName: String?, file: FileHandle?, parameter: ObjectCfgLoaderParameter?): Array<AssetDescriptor<Any>>? {
        return null
    }

    override fun loadSync(manager: AssetManager?, fileName: String?, file: FileHandle?, parameter: ObjectCfgLoaderParameter?): ObjectCfgMap {
        return cfgMap
    }

    override fun loadAsync(manager: AssetManager, fileName: String?, file: FileHandle, parameter: ObjectCfgLoaderParameter?) {
        val jsonValue = JsonReader().parse(file)
        var entry = jsonValue.child
        while (entry != null) {
            val type = ObjectType.valueOf(entry.name)
            cfgMap[type] = entry.child
            entry = entry.next
        }
    }
}