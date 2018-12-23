package com.quillraven.masamune.desktop

import com.badlogic.gdx.Application
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.quillraven.masamune.MainGame

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration().apply {
            vSyncEnabled = false
            width = 1280
            height = 720
        }
        LwjglApplication(MainGame(), config).logLevel = Application.LOG_DEBUG
    }
}
