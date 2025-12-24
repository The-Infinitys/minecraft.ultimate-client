package org.infinite.libs.graphics

import net.minecraft.client.DeltaTracker
import org.infinite.libs.graphics.graphics2d.structs.RenderCommand
import org.infinite.libs.graphics.graphics2d.structs.StrokeStyle
import java.util.concurrent.PriorityBlockingQueue
import kotlin.math.roundToInt

/**
 * MDN CanvasRenderingContext2D API を Minecraft GuiGraphics 上に再現するクラス
 */
class Graphics2D(
    deltaTracker: DeltaTracker,
    var zIndex: Int = 0,
) {
    private val capturedGameDelta: Float = deltaTracker.gameTimeDeltaTicks
    private val capturedRealDelta: Float = deltaTracker.realtimeDeltaTicks

    var strokeStyle: StrokeStyle? = null

    // 塗りつぶしの色（Canvas API風に Int で管理）
    var fillStyle: Int = 0xFFFFFFFF.toInt()

    private val commandQueue = PriorityBlockingQueue<RenderCommand>(100, compareBy { it.zIndex })

    // --- strokeRect ---

    fun strokeRect(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
    ) {
        val style = strokeStyle ?: return
        val (strokeColor, strokeWidthDouble) = style
        val strokeWidth = strokeWidthDouble.roundToInt()
        commandQueue.add(RenderCommand.DrawRectInt(x, y, width, height, strokeWidth, strokeColor, zIndex))
    }

    fun strokeRect(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
    ) {
        val style = strokeStyle ?: return
        val (strokeColor, strokeWidthDouble) = style
        val strokeWidth = strokeWidthDouble.toFloat()
        commandQueue.add(RenderCommand.DrawRectFloat(x, y, width, height, strokeWidth, strokeColor, zIndex))
    }

    fun strokeRect(
        x: Double,
        y: Double,
        width: Double,
        height: Double,
    ) {
        val style = strokeStyle ?: return
        val (strokeColor, strokeWidth) = style
        commandQueue.add(RenderCommand.DrawRectDouble(x, y, width, height, strokeWidth, strokeColor, zIndex))
    }

    // --- fillRect ---

    fun fillRect(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
    ) {
        commandQueue.add(RenderCommand.FillRectInt(x, y, width, height, fillStyle, zIndex))
    }

    fun fillRect(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
    ) {
        commandQueue.add(RenderCommand.FillRectFloat(x, y, width, height, fillStyle, zIndex))
    }

    fun fillRect(
        x: Double,
        y: Double,
        width: Double,
        height: Double,
    ) {
        commandQueue.add(RenderCommand.FillRectDouble(x, y, width, height, fillStyle, zIndex))
    }

    // --- Utilities ---

    fun gameDelta(): Float = capturedGameDelta

    fun realDelta(): Float = capturedRealDelta

    fun poll(): RenderCommand? = commandQueue.poll()
}
