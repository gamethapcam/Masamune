package com.quillraven.masamune.model

import com.badlogic.gdx.utils.JsonValue
import java.util.*

class ObjectCfgMap : EnumMap<EObjectType, JsonValue>(EObjectType::class.java)