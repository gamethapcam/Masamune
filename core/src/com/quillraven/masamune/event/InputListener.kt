package com.quillraven.masamune.event

interface InputListener {
    fun inputMove(percentX: Float, percentY: Float)

    fun inputAction()
}