package com.quillraven.masamune.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.Pool
import com.quillraven.masamune.MainGame

interface ISerializableComponent : Component, Pool.Poolable {
    fun write(json: Json)

    fun read(jsonData: JsonValue, game: MainGame)
}