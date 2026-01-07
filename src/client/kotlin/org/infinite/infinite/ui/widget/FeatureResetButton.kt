package org.infinite.infinite.ui.widget

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import org.infinite.InfiniteClient
import org.infinite.libs.core.features.Feature
import org.infinite.libs.graphics.Graphics2D
import org.infinite.libs.graphics.bundle.Graphics2DRenderer
import kotlin.math.PI

class FeatureResetButton(x: Int, y: Int, width: Int, height: Int, feature: Feature) :
    Button(
        x,
        y,
        width,
        height,
        Component.literal("Reset"),
        {
            val soundManager = Minecraft.getInstance().soundManager
            playButtonClickSound(soundManager)
            feature.reset()
        },
        DEFAULT_NARRATION,
    ) {
    override fun renderContents(
        guiGraphics: GuiGraphics,
        i: Int,
        j: Int,
        f: Float,
    ) {
        val graphics2DRenderer = Graphics2DRenderer(guiGraphics)
        render(graphics2DRenderer)
        graphics2DRenderer.flush()
    }

    private fun Graphics2D.renderResetIcon(x: Int, y: Int, width: Int, height: Int) =
        this.renderResetIcon(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())

    private fun Graphics2D.renderResetIcon(x: Float, y: Float, width: Float, height: Float) {
        // 1. 時間パラメータの取得 (0.0 ~ 1.0)
        val duration = 2000.0 // 2秒で1サイクル
        val t = (System.currentTimeMillis() % duration) / duration
        val colorScheme = InfiniteClient.theme.colorScheme
        val color = colorScheme.accentColor
        val centerX = x + width / 2f
        val centerY = y + height / 2f
        val angle = 2.0 * PI * t
        val angleF = angle.toFloat()
        val rX = width / 3f
        val rY = height / 3f
        val r = (rX + rY) / 2f
        this.push()
        this.rotateAt(angleF, centerX, centerY)
        this.fillStyle = color
        this.fillTriangle(centerX, centerY, centerX, centerY - rY * 2, centerX + rX, centerY - rY)
        this.beginPath()
        this.strokeStyle.width = 2f
        this.strokeStyle.color = color
        this.arc(centerX, centerY, r, (PI / 2.0).toFloat(), (3.0 * PI / 2.0).toFloat())
        this.strokePath()
        this.pop()
    }

    fun render(
        graphics2D: Graphics2D,
    ) {
        val theme = InfiniteClient.theme
        theme.renderBackGround(this.x, this.y, this.width, this.height, graphics2D, 0.8f)
        graphics2D.renderResetIcon(this.x, this.y, this.width, this.height)
    }
}
