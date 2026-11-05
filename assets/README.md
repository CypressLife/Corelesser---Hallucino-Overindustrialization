# 资源目录

这里是游戏的运行时资源根目录：

- 开发时 `lwjgl3:run` 会把它设为工作目录；
- 打包时它会被加进运行时 classpath（见根 `build.gradle.kts`）。

代码里直接写相对路径即可：

```kotlin
val texture = Texture(Gdx.files.internal("images/icon.png"))
```

目前目录是空的：VisUI 的默认皮肤打包在 `vis-ui` 的 jar 里。
以后的中文界面字体、图集、音效、关卡数据都放这里。
