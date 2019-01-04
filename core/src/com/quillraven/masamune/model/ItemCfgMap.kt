package com.quillraven.masamune.model

import com.badlogic.gdx.utils.JsonValue
import java.util.*

class ItemCfgMap : EnumMap<EItemType, JsonValue>(EItemType::class.java)