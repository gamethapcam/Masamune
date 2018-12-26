package com.quillraven.masamune.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.quillraven.masamune.MainGame

abstract class Q2DScreen : Screen {
    protected val game = Gdx.app.applicationListener as MainGame
    protected val stage = game.stage
}