package com.quillraven.masamune.event

import com.badlogic.ashley.core.Entity
import com.quillraven.masamune.model.EEquipType

interface ItemListener {
    fun itemSlotUpdated(slotIdx: Int, item: Entity?)

    fun inventoryResize(newSize: Int)

    fun equipSlotUpdated(type: EEquipType, item: Entity?)
}