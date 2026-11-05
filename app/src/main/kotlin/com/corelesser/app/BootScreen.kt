package com.corelesser.app

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.Version
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable

/**
 * 工程骨架的启动界面。
 *
 * 它只做一件事：证明 libGDX + VisUI + natives 这条链路是通的。
 * 真正的 UI 会被 build-1.5 的玩法界面替换掉。
 */
class BootScreen : ScreenAdapter() {

    private val stage = Stage(ScreenViewport())

    init {
        val table = VisTable().apply { setFillParent(true) }
        table.add(VisLabel("Corelesser - Hallucino-Overindustrialization")).row()
        table.add(VisLabel("build-1.5 / libGDX ${Version.VERSION}")).row()
        stage.addActor(table)
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.08f, 0.09f, 0.11f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        stage.dispose()
    }
}
