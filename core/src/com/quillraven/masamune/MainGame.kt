package com.quillraven.masamune

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.SkinLoader
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.viewport.FitViewport
import com.quillraven.masamune.ecs.ComponentMapper
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.event.GameEventManager
import com.quillraven.masamune.map.MapManager
import com.quillraven.masamune.physic.B2DUtils
import com.quillraven.masamune.physic.Q2DContactListener
import com.quillraven.masamune.screen.LoadingScreen
import com.quillraven.masamune.screen.Q2DScreen
import com.quillraven.masamune.serialization.Q2DSerializer


private const val TAG = "Main"
internal const val UNIT_SCALE = 1 / 32f
private const val SKIN_ATLAS_PATH = "ui/ui.atlas"
private const val SKIN_PATH = "ui/ui.json"

class MainGame : Q2DGame() {
    internal val gameViewPort by lazy { FitViewport(16f, 9f) }

    internal val shader by lazy {
        val prog = ShaderProgram(assetManager.fileHandleResolver.resolve("shader/vertex.glsl"), assetManager.fileHandleResolver.resolve("shader/fragment.glsl"))
        if (!prog.isCompiled) throw GdxRuntimeException("Could not compile shader ${prog.log}")
        prog
    }
    internal val batch by lazy { SpriteBatch(1000, shader) }
    internal val stage by lazy { Stage(FitViewport(16f / UNIT_SCALE, 9f / UNIT_SCALE), batch) }

    internal val skin by lazy {
        val resources = ObjectMap<String, Any>()
        val skinParameter = SkinLoader.SkinParameter(SKIN_ATLAS_PATH, resources)
        assetManager.load(SKIN_PATH, Skin::class.java, skinParameter)
        assetManager.finishLoading()
        assetManager.get(SKIN_PATH, Skin::class.java)
    }

    internal val ecsEngine by lazy { ECSEngine(this) }
    internal val cmpMapper by lazy { ComponentMapper() }

    internal val world by lazy {
        val contactListener = Q2DContactListener(this)
        World(Vector2(0f, 0f), true).apply { setContactListener(contactListener) }
    }
    internal val b2dUtils by lazy { B2DUtils(world) }

    internal val assetManager by lazy { AssetManager() }

    internal val gameEventManager by lazy { GameEventManager() }

    internal val mapManager by lazy { MapManager(this) }

    internal val json by lazy { Json(JsonWriter.OutputType.minimal) }
    internal val serializer by lazy { Q2DSerializer(this) }

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
        shader.dispose()
        stage.dispose()
        world.dispose()
        super.dispose()
    }
}
