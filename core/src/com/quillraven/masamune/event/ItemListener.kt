package com.quillraven.masamune.event

import com.badlogic.ashley.core.Entity

interface ItemListener {
    fun itemSlotUpdated(slotIdx: Int, item: Entity?)

    fun inventoryResize(newSize: Int)
}