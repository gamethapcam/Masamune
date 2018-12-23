package com.quillraven.masamune

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.ObjectMap
import com.quillraven.masamune.screen.Q2DScreen

private const val TAG = "Game"

abstract class Q2DGame : ApplicationListener {
    private var screen: Q2DScreen? = null
    private val screenCache by lazy { ObjectMap<Class<out Q2DScreen>, Q2DScreen>() }

    fun setScreen(type: Class<out Q2DScreen>, dispose: Boolean = false) {
        Gdx.app.debug(TAG, "Setting new screen to ${type.simpleName}")

        screen?.hide()
        if (dispose) {
            screen?.dispose()
        }

        screen = screenCache.get(type)
        if (screen == null) {
            Gdx.app.debug(TAG, "Creating new screen instance")
            screen = type.newInstance()
            screenCache.put(type, screen)
        }
        screen?.show()
        screen?.resize(Gdx.graphics.width, Gdx.graphics.height)
    }

    override fun render() {
        screen?.render(Math.min(0.25f, Gdx.graphics.rawDeltaTime))
    }

    override fun pause() {
        screen?.pause()
    }

    override fun resume() {
        screen?.resume()
    }

    override fun resize(width: Int, height: Int) {
        screen?.resize(width, height)
    }

    override fun dispose() {
        Gdx.app.debug(TAG, "Disposing ${screenCache.size} screen(s)")

        for (item in screenCache.values()) {
            item.hide()
            item.dispose()
        }
    }
}