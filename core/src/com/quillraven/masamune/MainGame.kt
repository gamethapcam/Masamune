package com.quillraven.masamune

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.SkinLoader
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.viewport.FitViewport
import com.quillraven.masamune.ecs.ComponentMapper
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.event.GameEventManager
import com.quillraven.masamune.map.MapManager
import com.quillraven.masamune.screen.LoadingScreen
import com.quillraven.masamune.screen.Q2DScreen


private const val TAG = "Main"
internal const val UNIT_SCALE = 1 / 32f

class MainGame : Q2DGame() {
    internal val gameViewPort by lazy { FitViewport(16f, 9f) }
    internal val batch by lazy { SpriteBatch() }
    internal val stage by lazy { Stage(FitViewport(16 * 32f, 9 * 32f), batch) }

    private val skinAtlasPath = "ui/neutralizer-ui.atlas"
    private val skinPath = "ui/neutralizer-ui.json"
    internal val skin by lazy {
        val resources = ObjectMap<String, Any>()
        val skinParameter = SkinLoader.SkinParameter(skinAtlasPath, resources)
        assetManager.load(skinPath, Skin::class.java, skinParameter)
        assetManager.finishLoading()
        assetManager.get(skinPath, Skin::class.java)
    }

    internal val ecsEngine by lazy { ECSEngine() }
    internal val cmpMapper by lazy { ComponentMapper() }

    internal val world by lazy { World(Vector2(0f, 0f), true) }
    internal val b2dUtils by lazy { B2DUtils(world) }

    internal val assetManager by lazy { AssetManager() }

    internal val gameEventManager by lazy { GameEventManager() }

    internal val mapManager by lazy { MapManager(this) }

    internal val preferences by lazy { Gdx.app.getPreferences("masamune") }

    internal val spriteCache by lazy { SpriteCache(this) }

    override fun initialize() {
        Gdx.input.inputProcessor = stage
    }

    override fun getFirstScreenType(): Class<out Q2DScreen> {
        return LoadingScreen::class.java
    }

    override fun dispose() {
        Gdx.app.debug(TAG, "Maximum sprites in batch: ${batch.maxSpritesInBatch}. Current batch size is 1000. Increase it if max is higher.")

        ecsEngine.dispose()
        batch.dispose()
        stage.dispose()
        world.dispose()
        super.dispose()
    }
}
