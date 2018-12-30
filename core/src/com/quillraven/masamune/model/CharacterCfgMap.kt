package com.quillraven.masamune.model

import com.badlogic.gdx.utils.JsonValue
import java.util.*

class CharacterCfgMap : EnumMap<ECharacterType, JsonValue>(ECharacterType::class.java)