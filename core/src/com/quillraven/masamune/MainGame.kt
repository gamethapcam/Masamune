package com.quillraven.masamune

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.FitViewport
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.event.GameEventManager
import com.quillraven.masamune.map.MapManager
import com.quillraven.masamune.screen.LoadingScreen
import com.quillraven.masamune.screen.Q2DScreen

private const val TAG = "Main"
internal const val UNIT_SCALE = 1 / 32f
internal val bodyDef = BodyDef()
internal val fixtureDef = FixtureDef()

internal fun resetBodyAndFixtureDef() {
    bodyDef.apply {
        type = BodyDef.BodyType.StaticBody
        position.set(Vector2.Zero)
        angle = 0f
        linearVelocity.set(Vector2.Zero)
        angularVelocity = 0f
        linearDamping = 0f
        angularDamping = 0f
        allowSleep = true
        awake = true
        fixedRotation = false
        bullet = false
        active = true
        gravityScale = 1f
    }

    fixtureDef.apply {
        shape = null
        friction = 0.2f
        restitution = 0f
        density = 0f
        isSensor = false
        filter.categoryBits = 0x0001
        filter.maskBits = -1
        filter.groupIndex = 0
    }
}

class MainGame : Q2DGame() {
    internal val gameViewPort by lazy { FitViewport(16f, 9f) }
    internal val batch by lazy { SpriteBatch() }
    internal val ecsEngine by lazy { ECSEngine() }

    internal val world by lazy { World(Vector2(0f, 0f), true) }

    internal val assetManager by lazy { AssetManager() }
    internal val gameEventManager by lazy { GameEventManager() }
    internal val mapManager by lazy { MapManager(this) }

    override fun initialize() {
        assetManager.setLoader(TiledMap::class.java, TmxMapLoader(assetManager.fileHandleResolver))
    }

    override fun getFirstScreenType(): Class<out Q2DScreen> {
        return LoadingScreen::class.java
    }

    override fun dispose() {
        Gdx.app.debug(TAG, "Maximum sprites in batch: ${batch.maxSpritesInBatch}. Current batch size is 1000. Increase it if max is higher.")

        ecsEngine.dispose()
        batch.dispose()
        world.dispose()
        super.dispose()
    }
}
