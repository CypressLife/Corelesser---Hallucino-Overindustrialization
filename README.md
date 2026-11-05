# Corelesser - Hallucino-Overindustrialization · build-1.5

> 本目录是 **《Corelesser - Hallucino-Overindustrialization》** 的 `build-1.5` 工作区。
> 项目总览、分支规则与许可证策略见项目根 README。

| | |
| --- | --- |
| 本 build | `build-1.5` |
| 定位 | 重写 / 总成：在 libGDX 官方工程结构上重新组织 build-1.0 验证过的系统 |
| 上一版 | `build-1.0`（已冻结，代码不迁移进本目录） |
| 平台 | 桌面（Windows / macOS / Linux） |
| 当前状态 | 工程骨架（Gradle + 启动链路已打通） |

---

## 技术栈

| 组件 | 版本 | 说明 |
| --- | --- | --- |
| Kotlin | 2.3.10 | 主语言 |
| JDK | 21（Gradle 工具链固定） | libGDX 1.13.x 字节码目标为 Java 8，用 21 编译兼容 |
| Gradle | 8.12（Wrapper，阿里云镜像） | 构建工具 |
| libGDX | 1.13.1 | 游戏框架 |
| KTX | 1.13.1-rc1 | libGDX 的 Kotlin 扩展（KtxGame、Scene2D DSL、协程等） |
| VisUI | 1.5.5 | Scene2D 皮肤与控件库 |
| kotlinx | coroutines 1.10.2 / serialization 1.10.0 / datetime 0.7.1 | 异步与存档序列化 |

> libGDX / KTX / VisUI 是**绑定关系**：KTX 1.13.1-rc1 基于 libGDX 1.13.1 与 VisUI 1.5.5 构建。
> 不要单独升级其中一个，否则容易遇到 `NoSuchMethodError`；升级时请一起改版本目录。

---

## 目录结构

```text
.
├── build.gradle.kts            # 根脚本：插件、JDK、所有模块的依赖都在这里
├── settings.gradle.kts         # 模块声明与仓库（阿里云镜像 + 官方 + JitPack）
├── gradle/libs.versions.toml   # 版本目录：依赖版本的唯一来源
├── gradle.properties           # 构建缓存、配置缓存、JVM 参数
├── assets/                     # 运行时资源根目录（开发时作为工作目录）
├── core/                       # 核心逻辑（与平台、UI 无关）
│   └── src/test/kotlin/com/corelesser/core/SanityTest.kt
├── app/                        # 游戏前端：Game / Screen / Scene2D / VisUI
│   └── src/main/kotlin/com/corelesser/app/
│       ├── CorelesserGame.kt   # 游戏入口（加载 VisUI、挂载 Screen）
│       └── BootScreen.kt       # 冒烟启动界面
└── lwjgl3/                     # 桌面启动器
    └── src/main/kotlin/com/corelesser/lwjgl3/Lwjgl3Launcher.kt
```

模块职责：

- **`core`** 只放核心逻辑，依赖里没有 VisUI / Scene2D / FreeType，不碰窗口和 natives；
- **`app`** 放游戏前端（`Game`、`Screen`、Scene2D/VisUI 界面），依赖 `core`；
- **`lwjgl3`** 只负责原生窗口、本地库（`natives-desktop`）与启动 `app`。

以后加 Android / iOS / Web，只需要新增一个 launcher 模块，`core` 与 `app` 不用改。

---

## 构建与运行

### 环境要求

- **JDK 21**，并让 `JAVA_HOME` 指向它（本机为 `D:\java21`）。
- 不需要单独安装 Gradle 或 libGDX：Wrapper 与依赖会自动下载（已配阿里云镜像）。

### 命令（Windows 用 `gradlew.bat`）

```bash
# 启动桌面版（会打开 VisUI 冒烟窗口）
./gradlew lwjgl3:run

# 只编译并打包，不运行
./gradlew build

# 跑全部检查（含 core 冒烟测试）
./gradlew check

# 清理构建产物
./gradlew clean
```

### 在 IntelliJ IDEA 中

1. 打开**本目录**（不是 `core/`），等待 Gradle 同步完成。
2. 运行 `lwjgl3` 模块的 `Lwjgl3LauncherKt`，或 Gradle 面板里的 `lwjgl3 > Tasks > application > run`。

---

## 依赖配置

依赖**全部集中在根 `build.gradle.kts`** 的 `project(":core")` / `project(":app")` /
`project(":lwjgl3")` 块里，模块目录下没有 `build.gradle.kts`。
这种写法来自 libGDX 官方 `gdx-setup` 生成的工程。

- **版本**：只改 `gradle/libs.versions.toml`。
- **加/删依赖**：只改根脚本；同一个 bundle 里的库按组引入。
- **本地库（natives）**：classifier 无法写进版本目录，因此在 `:lwjgl3` 里用
  `com.badlogicgames.gdx:gdx-platform:<version>:natives-desktop` 这类完整坐标声明。
- **依赖分组**：版本目录里按职责分成 `gdx`、`ktx`（核心逻辑）与 `frontend`（前端）。
  `:core` 用 `api` 暴露逻辑依赖，`:app` 用 `api(project(":core"))` + `frontend`，
  `:lwjgl3` 只拿 `:app`、后端与 natives。`kotlinx` 在 `:core` 里是 `implementation`。

依赖版本沿用 `build-1.0` 的清单。其中 `gdx-bullet`、`gdx-gltf`、`gdx-ai`、`gdx-box2d`、
`box2dlights`、`gdx-controllers`、`kryonet`、`colorful` 在 build-1.0 的代码里**没有任何引用**，
本 build 先把线接好；不需要的可以直接从 `gradle/libs.versions.toml` 的 `[bundles]` 里删掉。

---

## 资源

`assets/` 是运行时资源根目录：

- 开发时 `lwjgl3:run` 以它为工作目录；
- 打包时它会被加进运行时 classpath（见根脚本 `sourceSets`）。

代码里写相对路径即可：`Gdx.files.internal("images/icon.png")`。

---

## 当前进度

| 项目 | 状态 |
| --- | --- |
| Gradle 多模块工程（`core` + `app` + `lwjgl3`） | ✅ 完成 |
| libGDX / KTX / VisUI 依赖接线 | ✅ 完成并实际编译、打包验证 |
| VisUI 冒烟界面 | ✅ 可运行（`./gradlew lwjgl3:run`） |
| 玩法代码从 build-1.0 迁移 | ⬜ 未开始（按约定**不直接搬运 1.0 文件**） |
| 中文 UI 字体（FreeType） | ⬜ 未开始 |
| 地图 / 区块系统 | ⬜ 未开始 |
| 存档（`next_ID`、配方状态） | ⬜ 未开始 |

### 已知限制

- VisUI 自带皮肤只含拉丁字形，中文界面需要另外用 FreeType 加载中文字体。
- 目前只有桌面 launcher；Android / iOS / Web 尚未配置。
- 测试只有 `core` 里的一个冒烟用例，玩法逻辑尚无测试。

---

## 许可证

**待定**。直接依赖全部是宽松型（Apache-2.0 / CC0-1.0 / BSD-3-Clause），
与 Apache-2.0、MIT、MPL-2.0、GPL-3.0 均兼容；推荐本 build 采用 **Apache-2.0**。

注意发行包里还会带入两个传递依赖：`jlayer`、`jorbis`（libGDX 桌面后端的音频解码库），
它们是 **LGPL-2.1**。LGPL 不要求你的游戏源码开源，但分发时需要保留它们自己的许可证与版权声明，
并保证这两个库是可替换的独立 jar（本工程默认就是 classpath 方式）。

确认许可证后会在本目录放入 `LICENSE` 与第三方声明（`NOTICE` / `THIRD-PARTY-LICENSES`）。
在许可证落地前，请把本目录视为“保留所有权利”。

---

## 致谢

- [libGDX](https://libgdx.com/) —— 跨平台游戏框架
- [KTX](https://libktx.github.io/) —— libGDX 的 Kotlin 扩展
- [VisUI](https://github.com/kotcrab/vis-ui) —— Scene2D UI 库
