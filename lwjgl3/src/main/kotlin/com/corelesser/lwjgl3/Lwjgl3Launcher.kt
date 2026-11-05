package com.corelesser.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.corelesser.app.CorelesserGame

/**
 * 桌面启动器。
 *
 * 这个类刻意保持很薄：窗口参数写在这里，游戏内容全部在 :core。
 */
fun main() {
    val configuration = Lwjgl3ApplicationConfiguration().apply {
        setTitle("Corelesser - Hallucino-Overindustrialization")
        setWindowedMode(1280, 720)
        setWindowSizeLimits(960, 540, -1, -1)
        useVsync(true)
        setForegroundFPS(60)
        // RGBA 8/8/8/8，深度 16，模板 0，4x MSAA
        setBackBufferConfig(8, 8, 8, 8, 16, 0, 4)
    }

    Lwjgl3Application(CorelesserGame(), configuration)
}
