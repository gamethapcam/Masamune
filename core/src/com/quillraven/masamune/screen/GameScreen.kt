package com.quillraven.masamune.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape

class GameScreen : Q2DScreen() {
    override fun hide() {

    }

    override fun show() {
        //TODO testing
        val bodyDef = BodyDef().apply {
            position.set(8f, 4.5f)
            type = BodyDef.BodyType.StaticBody
        }
        val body = game.world.createBody(bodyDef)

        val box = PolygonShape()
        box.setAsBox(7f, 1f)
        val fixtureDef = FixtureDef().apply {
            shape = box
        }
        body.createFixture(fixtureDef)
        box.dispose()
    }

    override fun render(delta: Float) {
        game.ecsEngine.update(delta)

        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            game.setScreen(LoadingScreen::class.java)
        }
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun resize(width: Int, height: Int) {
        game.gameViewPort.update(width, height, true)
    }

    override fun dispose() {

    }
}