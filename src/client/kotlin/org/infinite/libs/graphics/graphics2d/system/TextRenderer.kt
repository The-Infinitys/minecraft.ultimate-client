package org.infinite.libs.graphics.graphics2d.system

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import org.infinite.libs.interfaces.MinecraftInterface

class TextRenderer(private val guiGraphics: GuiGraphics) : MinecraftInterface() {
    fun text(font: Font, text: String, x: Float, y: Float, color: Int, size: Float = 8.0f, shadow: Boolean = false) {
        val poseStack = guiGraphics.pose()
        poseStack.pushMatrix()

        // 1. まず指定の座標(x, y)へ移動
        poseStack.translate(x, y)
        val fontSize = size / client.font.lineHeight.toFloat()
        // 2. その場でスケーリング（サイズ変更）
        poseStack.scale(fontSize, fontSize)

        // 描画（座標は0, 0でOK）
        guiGraphics.drawString(font, text, 0, 0, color, shadow)

        poseStack.popMatrix()
    }
}
