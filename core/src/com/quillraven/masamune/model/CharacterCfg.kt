package com.quillraven.masamune.model

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.JsonReader
import java.util.*

enum class ECharacterType {
    HERO, ELDER
}

data class CharacterCfg(
        val type: ECharacterType,
        val bodyType: BodyDef.BodyType,
        val texture: String,
        val width: Float,
        val height: Float,
        val speed: Float,
        val flip: Boolean
)

class CharacterCfgMap : EnumMap<ECharacterType, CharacterCfg>(ECharacterType::class.java)

class CharacterCfgLoader(resolver: FileHandleResolver) : AsynchronousAssetLoader<CharacterCfgMap, CharacterCfgLoader.CharacterCfgLoaderParameter>(resolver) {
    private val cfgMap = CharacterCfgMap()

    class CharacterCfgLoaderParameter : AssetLoaderParameters<CharacterCfgMap>()

    override fun getDependencies(fileName: String?, file: FileHandle?, parameter: CharacterCfgLoaderParameter?): Array<AssetDescriptor<Any>>? {
        return null
    }

    override fun loadSync(manager: AssetManager?, fileName: String?, file: FileHandle?, parameter: CharacterCfgLoaderParameter?): CharacterCfgMap {
        return cfgMap
    }

    override fun loadAsync(manager: AssetManager, fileName: String?, file: FileHandle, parameter: CharacterCfgLoaderParameter?) {
        val jsonValue = JsonReader().parse(file)
        var entry = jsonValue.child
        while (entry != null) {
            val type = ECharacterType.valueOf(entry.name)
            cfgMap[type] = CharacterCfg(
                    type,
                    BodyDef.BodyType.valueOf(entry.getString("bodyType")),
                    entry.getString("texture"),
                    entry.getFloat("width", 1f),
                    entry.getFloat("height", 1f),
                    entry.getFloat("speed", 0f),
                    entry.getBoolean("flip", true))
            entry = entry.next
        }
    }

}