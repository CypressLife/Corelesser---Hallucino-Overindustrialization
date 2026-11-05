package com.corelesser.core

import com.badlogic.gdx.math.Vector2
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * 冒烟测试：确认 :core 的编译和测试链路（Kotlin + JUnit 5 + libGDX）是通的。
 */
class SanityTest {

    @Test
    fun `libGDX classes are on the test classpath`() {
        val result = Vector2(1f, 2f).add(2f, 3f)
        assertEquals(3f, result.x)
        assertEquals(5f, result.y)
    }
}
