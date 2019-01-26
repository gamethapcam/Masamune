package com.quillraven.masamune.model

enum class EEquipType {
    HELMET, WEAPON, ARMOR, SHIELD, GLOVES, BOOTS, RING, NECKLACE, UNDEFINED // undefined must be the last one because otherwise accessing equipment slots has to be reduced by one
}