package com.quillraven.masamune.event

import com.badlogic.ashley.core.Entity
import com.quillraven.masamune.model.EEquipType

interface ItemListener {
    fun inventorySlotUpdated(slotIdx: Int, item: Entity?) {}

    fun inventoryResize(newSize: Int) {}

    fun equipSlotUpdated(entity: Entity, type: EEquipType, prevItem: Entity?, newItem: Entity?) {}

    fun useItem(entity: Entity, item: Entity) {}
}