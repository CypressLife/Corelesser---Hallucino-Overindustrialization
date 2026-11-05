package com.corelesser.app

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.Version
import com.kotcrab.vis.ui.VisUI
import ktx.app.KtxGame

/**
 * 游戏入口（跨平台）。
 *
 * 启动器在 :lwjgl3 模块里；以后新增 Android / iOS / Web 时，
 * 只需为每个平台写一个新的 launcher，这个类不用改。
 */
class CorelesserGame : KtxGame<Screen>() {

    override fun create() {
        Gdx.app.log("Corelesser", "build-1.5 启动，libGDX ${Version.VERSION}")

        // VisUI 的皮肤随 jar 一起发布，不需要往 assets 里放皮肤文件。
        VisUI.load(VisUI.SkinScale.X1)

        addScreen(BootScreen())
        setScreen<BootScreen>()
    }

    override fun dispose() {
        super.dispose()
        VisUI.dispose()
    }
}
