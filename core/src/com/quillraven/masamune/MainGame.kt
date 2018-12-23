package com.quillraven.masamune

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2D
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.FitViewport
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.event.GameEventManager
import com.quillraven.masamune.map.MapManager
import com.quillraven.masamune.screen.LoadingScreen

private const val TAG = "Main"
internal const val UNIT_SCALE = 1 / 32f

class MainGame : Q2DGame() {
    internal val gameViewPort by lazy { FitViewport(16f, 9f) }
    internal val batch by lazy { SpriteBatch() }
    internal val ecsEngine by lazy { ECSEngine() }
    internal val world by lazy { World(Vector2(0f, 0f), true) }
    internal val assetManager by lazy { AssetManager() }
    internal val gameEventManager by lazy { GameEventManager() }
    internal val mapManager by lazy { MapManager() }

    override fun create() {
        // important to make any call to the ecsEngine to initialize it first because otherwise it might be created
        // to late and some game events do not get send to the systems
        ecsEngine.clearPools()

        Box2D.init()

        assetManager.setLoader(TiledMap::class.java, TmxMapLoader(assetManager.fileHandleResolver))

        setScreen(LoadingScreen::class.java)
    }

    override fun dispose() {
        Gdx.app.debug(TAG, "Maximum sprites in batch: ${batch.maxSpritesInBatch}. Current batch size is 1000. Increase it if max is higher.")

        ecsEngine.dispose()
        batch.dispose()
        world.dispose()
        super.dispose()
    }
}
