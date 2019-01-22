package com.quillraven.masamune.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Disposable
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.SpriteCache
import com.quillraven.masamune.UNIT_SCALE
import com.quillraven.masamune.ecs.component.ActionableComponent
import com.quillraven.masamune.ecs.component.RemoveComponent
import com.quillraven.masamune.ecs.component.RenderComponent
import com.quillraven.masamune.ecs.component.TransformComponent
import com.quillraven.masamune.event.MapEvent
import com.quillraven.masamune.event.MapListener

class GameRenderSystem constructor(game: MainGame) : SortedIteratingSystem(Family.all(TransformComponent::class.java, RenderComponent::class.java).exclude(RemoveComponent::class.java).get(), YComparator(game)), MapListener, Disposable {
    private class YComparator constructor(game: MainGame) : Comparator<Entity> {
        private val transformCmpMapper = game.cmpMapper.transform

        override fun compare(o1: Entity, o2: Entity): Int {
            return Math.signum(transformCmpMapper.get(o2).interpolatedY - transformCmpMapper.get(o1).interpolatedY).toInt()
        }
    }

    private val transformCmpMapper = game.cmpMapper.transform
    private val renderCmpMapper = game.cmpMapper.render

    private val viewport = game.gameViewPort
    private val camera = viewport.camera as OrthographicCamera
    private val shader = game.shaderOutline
    private val batch = game.batch
    private val spriteCache = SpriteCache(game)

    private val mapRenderer = OrthogonalTiledMapRenderer(null, UNIT_SCALE, batch)
    private lateinit var bgdLayers: Array<TiledMapTileLayer>
    private lateinit var fgdLayers: Array<TiledMapTileLayer>

    private val actionableEntities by lazy { engine.getEntitiesFor(Family.all(TransformComponent::class.java, RenderComponent::class.java, ActionableComponent::class.java).get()) }
    // 1f / ... means the outline is exactly one pixel thick. higher values create a thicker outline
    private val shaderOutlineStepX = 1.5f / spriteCache.texWidth
    private val shaderOutlineStepY = 1.5f / spriteCache.texHeight

    private val clipBounds = Rectangle()
    private val scissors = Rectangle()

    init {
        game.gameEventManager.addMapListener(this)
    }

    override fun entityAdded(entity: Entity?) {
        super.entityAdded(entity)

        // set sprite if needed
        val renderCmp = renderCmpMapper.get(entity)
        if (renderCmp != null && !renderCmp.texture.isBlank()) {
            renderCmp.sprite = spriteCache.getSprite(renderCmp.texture, renderCmp.texIndex)
        }
    }

    override fun update(deltaTime: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.setColor(1f, 1f, 1f, 1f)

        viewport.apply()
        mapRenderer.setView(camera)

        clipBounds.set(camera.position.x - camera.viewportWidth * 0.5f, camera.position.y - camera.viewportHeight * 0.5f, viewport.worldWidth, viewport.worldHeight)
        ScissorStack.calculateScissors(camera, viewport.screenX.toFloat(), viewport.screenY.toFloat(), viewport.screenWidth.toFloat(), viewport.screenHeight.toFloat(), batch.transformMatrix, clipBounds, scissors)
        ScissorStack.pushScissors(scissors)

        AnimatedTiledMapTile.updateAnimationBaseTime()
        batch.begin()
        for (layer in bgdLayers) {
            mapRenderer.renderTileLayer(layer)
        }
        forceSort()
        super.update(deltaTime)
        for (layer in fgdLayers) {
            mapRenderer.renderTileLayer(layer)
        }
        batch.end()

        if (actionableEntities.size() > 0) {
            batch.shader = shader
            batch.begin()
            shader.setUniformf("stepX", shaderOutlineStepX)
            shader.setUniformf("stepY", shaderOutlineStepY)
            shader.setUniformf("outlineColor", Color.GREEN)
            for (entity in actionableEntities) {
                processEntity(entity, deltaTime)
            }
            batch.end()
            batch.shader = null
        }

        ScissorStack.popScissors()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transformCmp = transformCmpMapper.get(entity)
        val renderCmp = renderCmpMapper.get(entity)

        renderCmp.sprite.apply {
            flip((renderCmp.flipX && !isFlipX) || (!renderCmp.flipX && isFlipX), (renderCmp.flipY && !isFlipY) || (!renderCmp.flipY && isFlipY))
            setBounds(transformCmp.interpolatedX - renderCmp.width * 0.5f, transformCmp.interpolatedY - transformCmp.height * 0.5f, renderCmp.width, renderCmp.height)
            setOriginCenter()
            rotation = transformCmp.interpolatedAngle * MathUtils.radDeg
            draw(batch)
        }
    }

    override fun mapChanged(event: MapEvent) {
        mapRenderer.map = event.map
        bgdLayers = event.bgdLayers
        fgdLayers = event.fgdLayers
    }

    override fun dispose() {
        mapRenderer.dispose()
    }
}