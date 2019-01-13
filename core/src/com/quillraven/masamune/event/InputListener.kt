package com.quillraven.masamune.event

interface InputListener {
    fun inputMove(percentX: Float, percentY: Float) {}

    fun inputAction() {}

    fun inputShowInventory() {}

    fun inputShowItem(slotIdx: Int) {}

    fun inputItemMoved(fromSlotIdx: Int, toSlotIdx: Int) {}
}