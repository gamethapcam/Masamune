package com.quillraven.masamune.event

import com.badlogic.ashley.core.Entity

interface ItemListener {
    fun itemMoved(fromSlotIdx: Int, toSlotIdx: Int)

    fun itemSlotUpdated(slotIdx: Int, item: Entity?)
}