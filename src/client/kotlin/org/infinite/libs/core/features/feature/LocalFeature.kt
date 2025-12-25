package org.infinite.libs.core.features.feature

import org.infinite.libs.core.TickInterface
import org.infinite.libs.core.features.Feature
import org.infinite.libs.graphics.Graphics2D
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
open class LocalFeature :
    Feature(),
    TickInterface {
    private val enabled = AtomicBoolean(false)
    fun isEnabled(): Boolean = enabled.load()
    fun enable() = enabled.store(true)
    fun disable() = enabled.store(false)
    fun toggle() = if (isEnabled()) disable() else enable()
    open fun onConnected() {
    }

    open fun onDisconnected() {
    }

    override fun onStartTick() {
    }

    override fun onEndTick() {
    }

    data class RenderPriority(var start: Int, var end: Int)

    val renderPriority = RenderPriority(0, 0)
    open fun onStartUiRendering(graphics2D: Graphics2D): Graphics2D = graphics2D
    open fun onEndUiRendering(graphics2D: Graphics2D): Graphics2D = graphics2D
}
