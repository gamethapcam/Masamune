package com.quillraven.masamune.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.quillraven.masamune.map.EMapType
import com.quillraven.masamune.model.ConversationCache
import com.quillraven.masamune.model.ConversationLoader
import com.quillraven.masamune.model.ObjectCfgLoader
import com.quillraven.masamune.model.ObjectCfgMap
import com.quillraven.masamune.ui.LoadingUI

class LoadingScreen : Q2DScreen() {
    private val loadingUI = LoadingUI(game)
    private val assetManager = game.assetManager

    override fun hide() {
        stage.clear()
    }

    override fun show() {
        assetManager.load("textures.atlas", TextureAtlas::class.java)

        assetManager.setLoader(ObjectCfgMap::class.java, ObjectCfgLoader(assetManager.fileHandleResolver))
        assetManager.load("cfg/character.json", ObjectCfgMap::class.java)
        assetManager.load("cfg/object.json", ObjectCfgMap::class.java)
        assetManager.load("cfg/item.json", ObjectCfgMap::class.java)

        assetManager.setLoader(ConversationCache::class.java, ConversationLoader(assetManager.fileHandleResolver))
        assetManager.load("conversation/allConversations.json", ConversationCache::class.java)

        assetManager.setLoader(TiledMap::class.java, TmxMapLoader(assetManager.fileHandleResolver))
        for (mapType in EMapType.values()) {
            if (mapType.filePath.isBlank()) {
                continue
            }
            assetManager.load(mapType.filePath, TiledMap::class.java)
        }

        stage.addActor(loadingUI)

        stage.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (assetManager.progress >= 1f) {
                    stage.removeListener(this)
                    game.setScreen(GameScreen::class.java, true)
                }
                return true
            }
        })
    }

    override fun render(delta: Float) {
        assetManager.update()
        loadingUI.setProgress(assetManager.progress)

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.viewport.apply(true)
        stage.act()
        stage.draw()
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {

    }
}