// Gradle 构建入口：声明模块、插件仓库与依赖仓库。
// https://docs.gradle.org/current/userguide/settings_file_basics.html
//
// 模块结构与 libGDX 官方推荐一致：
//   :core   —— 与平台无关的游戏代码（玩法逻辑都在这里）
//   :lwjgl3 —— 桌面启动器，只负责窗口、natives 与启动 :core
// 以后要加 Android / iOS / Web，只需要新增一个 launcher 模块，:core 不用改。

pluginManagement {
    repositories {
        // 国内网络优先走阿里云镜像；镜像缺包时自动回落到官方仓库。
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        maven("https://maven.aliyun.com/repository/central")
        mavenCentral()

        // gdx-gltf 只发布在 JitPack 上；限定 group，避免所有依赖都去 JitPack 查询。
        maven("https://jitpack.io") {
            content {
                includeGroup("com.github.mgsx-dev.gdx-gltf")
            }
        }
    }
}

plugins {
    // 本机缺少目标 JDK 时由 Foojay 自动下载，而不是直接报错。
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

rootProject.name = "corelesser-hallucino-overindustrialization"

include(":core")
include(":app")
include(":lwjgl3")
