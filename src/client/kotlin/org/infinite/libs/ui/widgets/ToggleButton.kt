package org.infinite.libs.ui.widgets

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import org.infinite.InfiniteClient
import kotlin.math.sin

class ToggleButton(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    private var state: Boolean,
    private var isEnabled: Boolean,
    private val onToggle: (Boolean) -> Unit,
) : Button(
    x,
    y,
    width,
    height,
    Component.empty(), // ラベルは空（描画ロジックでカスタムするため）
    { button ->
        // --- onPressのコンストラクタに処理を移動 ---
        val tb = button as ToggleButton
        if (tb.isEnabled) {
            tb.state = !tb.state
            tb.onToggle(tb.state)
            tb.animationStartTime = System.currentTimeMillis()
        }
    },
    DEFAULT_NARRATION,
) {
    private var animationStartTime: Long = -1L
    private val animationDuration = 200L

    override fun renderContents(
        context: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
    ) {
        val colorScheme = InfiniteClient.theme.colorScheme

        // 背景色の決定
        val backgroundColor = when {
            !isEnabled -> colorScheme.backgroundColor
            state -> if (isHovered) colorScheme.greenColor else colorScheme.accentColor
            else -> if (isHovered) colorScheme.secondaryColor else colorScheme.backgroundColor
        }

        val knobSize = height - 4
        val barWidth = (knobSize * 2).toFloat()
        val barHeight = height.toFloat() / 2.5f
        val barY = (y + (height - barHeight.toInt()) / 2).toFloat()
        val barX = (x + (width - barWidth.toInt()) / 2).toFloat()

        // 背景バーの描画
        context.fill(barX.toInt(), barY.toInt(), (barX + barWidth).toInt(), (barY + barHeight).toInt(), backgroundColor)

        // アニメーション計算
        val startKnobX = if (!state) barX + barWidth - knobSize - 2 else barX + 2
        val endKnobX = if (state) barX + barWidth - knobSize - 2 else barX + 2

        val currentKnobX = if (animationStartTime == -1L) {
            endKnobX
        } else {
            val currentTime = System.currentTimeMillis()
            val animProgress = (currentTime - animationStartTime).toFloat() / animationDuration
            if (animProgress < 1.0f) {
                val easedProgress = sin(animProgress * Math.PI / 2).toFloat()
                startKnobX + (endKnobX - startKnobX) * easedProgress
            } else {
                animationStartTime = -1L
                endKnobX
            }
        }

        val knobY = (y + 2).toFloat()
        val knobBorder = 2
        val knobBorderColor = if (isEnabled) colorScheme.accentColor else colorScheme.backgroundColor
        val knobInnerColor = if (isHovered) colorScheme.accentColor else colorScheme.foregroundColor

        // 外枠ノブ
        context.fill(currentKnobX.toInt(), knobY.toInt(), (currentKnobX + knobSize).toInt(), (knobY + knobSize).toInt(), knobBorderColor)

        // 内側ノブ
        context.fill(
            (currentKnobX + knobBorder).toInt(),
            (knobY + knobBorder).toInt(),
            (currentKnobX + knobSize - knobBorder).toInt(),
            (knobY + knobSize - knobBorder).toInt(),
            knobInnerColor,
        )
    }
}
