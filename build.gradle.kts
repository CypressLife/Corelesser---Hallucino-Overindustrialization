// 根构建脚本：集中声明插件、JDK 与所有模块依赖。
//
// 这种“依赖全部写在根 build.gradle.kts 的 project(...) 块里”的组织方式
// 来自 libGDX 官方 gdx-setup 生成的工程，模块目录下不再放 build.gradle.kts。
// 好处是：升级依赖只改 gradle/libs.versions.toml，加/删依赖只改本文件。
//
// 版本目录：gradle/libs.versions.toml
// 模块   ：:core（核心逻辑）、:app（游戏前端）、:lwjgl3（桌面启动器）

import org.gradle.api.plugins.JavaApplication
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

plugins {
    // 插件只在根声明版本并 apply false，由下面的模块按需启用。
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

// libGDX 的 natives 需要 classifier，版本目录表达不了，这里取出统一使用。
val gdxVersion: String = libs.versions.gdx.get()

// 版本目录访问器只在根脚本作用域可用（子项目没有 libs 扩展），
// 先在这里解析成 Provider，供下面的 project(...) 块使用。
val gdxBundle = libs.bundles.gdx
val ktxBundle = libs.bundles.ktx
val frontendBundle = libs.bundles.frontend
val kotlinxBundle = libs.bundles.kotlinx
val kryonetLib = libs.kryonet
val colorfulLib = libs.colorful
val kotlinTestLib = libs.kotlin.test
val junitJupiterLib = libs.junit.jupiter
val gdxBackendLwjgl3 = libs.gdx.backend.lwjgl3
val gdxControllersDesktop = libs.gdx.controllers.desktop

allprojects {
    group = "com.corelesser"
    version = "1.5.0"
}

subprojects {
    // 所有 Kotlin 模块统一使用 JDK 21：
    // 避免 Gradle 进程的 JDK（JAVA_HOME）和编译用的 JDK 不一致。
    plugins.withId("org.jetbrains.kotlin.jvm") {
        extensions.configure<KotlinJvmProjectExtension> {
            jvmToolchain(21)
            compilerOptions {
                // 显式指定，避免 Kotlin(默认 1.8) 与 Java(21) 的 target 不一致报错。
                jvmTarget.set(JvmTarget.JVM_21)
            }
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        testLogging {
            events("failed", "skipped")
        }
    }
}

// ---------------------------------------------------------------------------
// :core —— 核心逻辑（与平台无关，不含 UI）
// ---------------------------------------------------------------------------
project(":core") {
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    dependencies {
        // api：:app / :lwjgl3 也能看到这些类型（核心类型会随 api 传递）。
        // 依赖清单来自 build-1.0；目前其中部分库尚未被代码使用，但先接好线，
        // 需要精简时删掉对应 bundle 条目即可。UI 相关库在 :app 的 frontend bundle 里。
        "api"(gdxBundle)
        "api"(ktxBundle)
        "api"(kryonetLib)
        "api"(colorfulLib)

        // implementation：只在 :core 内部使用，不暴露给启动器。
        "implementation"(kotlinxBundle)

        "testImplementation"(kotlinTestLib)
        "testImplementation"(junitJupiterLib)
    }
}

// ---------------------------------------------------------------------------
// :app —— 游戏前端（Game / Screen / Scene2D / VisUI）
// ---------------------------------------------------------------------------
project(":app") {
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        // 前端依赖核心逻辑；用 api 让 :lwjgl3 也能看到 :core 暴露的类型。
        "api"(project(":core"))
        "api"(frontendBundle)
    }
}

// ---------------------------------------------------------------------------
// :lwjgl3 —— 桌面（Windows / macOS / Linux）启动器
// ---------------------------------------------------------------------------
project(":lwjgl3") {
    apply(plugin = "application")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        "implementation"(project(":app"))

        // LWJGL3 后端与手柄后端。
        "implementation"(gdxBackendLwjgl3)
        "implementation"(gdxControllersDesktop)

        // 平台本地库：缺失会在运行时抛 UnsatisfiedLinkError。
        "runtimeOnly"("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
        "runtimeOnly"("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop")
        "runtimeOnly"("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-desktop")
        "runtimeOnly"("com.badlogicgames.gdx:gdx-bullet-platform:$gdxVersion:natives-desktop")
    }

    extensions.configure<JavaApplication> {
        mainClass.set("com.corelesser.lwjgl3.Lwjgl3LauncherKt")
        applicationName = "Corelesser"
    }

    // 把仓库根目录的 assets/ 打进运行时 classpath，
    // 这样 Gdx.files.internal("...") 在开发运行和打包分发时都能找到资源。
    extensions.configure<SourceSetContainer> {
        named("main") {
            resources.srcDir(rootProject.file("assets"))
        }
    }

    tasks.named<JavaExec>("run") {
        // 开发时以 assets/ 为工作目录，改贴图/字体不用重新打包。
        workingDir = rootProject.file("assets")

        // macOS 上 GLFW 必须在主线程初始化。
        if (System.getProperty("os.name").orEmpty().startsWith("Mac", ignoreCase = true)) {
            jvmArgs("-XstartOnFirstThread")
        }
    }
}
