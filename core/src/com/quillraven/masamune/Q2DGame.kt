package com.quillraven.masamune

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.ObjectMap
import com.quillraven.masamune.screen.Q2DScreen

private const val TAG = "Game"

abstract class Q2DGame : ApplicationListener {
    private lateinit var screen: Q2DScreen
    private val screenCache by lazy { ObjectMap<Class<out Q2DScreen>, Q2DScreen>() }

    override fun create() {
        initialize()
        val firstScreenType = getFirstScreenType()
        Gdx.app.debug(TAG, "Initializing with screen ${firstScreenType.simpleName}")
        screen = firstScreenType.newInstance().apply {
            show()
            resize(Gdx.graphics.width, Gdx.graphics.height)
            screenCache.put(this.javaClass, this)
        }
    }

    abstract fun initialize()

    abstract fun getFirstScreenType(): Class<out Q2DScreen>

    fun setScreen(type: Class<out Q2DScreen>, dispose: Boolean = false) {
        Gdx.app.debug(TAG, "Setting new screen to ${type.simpleName}")

        screen.hide()
        if (dispose) {
            screen.dispose()
            screenCache.remove(screen.javaClass)
        }

        if (!screenCache.containsKey(type)) {
            Gdx.app.debug(TAG, "Creating new screen instance")
            screen = type.newInstance()
            screenCache.put(type, screen)
        } else {
            screen = screenCache.get(type)
        }
        screen.show()
        screen.resize(Gdx.graphics.width, Gdx.graphics.height)
    }

    override fun render() {
        screen.render(Math.min(1 / 30f, Gdx.graphics.deltaTime))
    }

    override fun pause() {
        screen.pause()
    }

    override fun resume() {
        screen.resume()
    }

    override fun resize(width: Int, height: Int) {
        screen.resize(width, height)
    }

    override fun dispose() {
        Gdx.app.debug(TAG, "Disposing ${screenCache.size} screen(s)")

        for (item in screenCache.values()) {
            item.hide()
            item.dispose()
        }
    }
}