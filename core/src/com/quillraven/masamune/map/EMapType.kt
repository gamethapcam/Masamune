package com.quillraven.masamune.map

enum class EMapType constructor(internal val filePath: String) {
    UNDEFINED(""),
    MAP01("map/map_01.tmx"),
    MAP02("map/map_02.tmx")
}