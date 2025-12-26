package org.infinite.libs.graphics.graphics2d.system

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import org.infinite.libs.graphics.graphics2d.text.IModernFontManager
import org.infinite.libs.graphics.text.fromFontSet
import org.infinite.libs.interfaces.MinecraftInterface
import org.infinite.mixin.graphics.MinecraftAccessor

class TextRenderer(private val guiGraphics: GuiGraphics) : MinecraftInterface() {
    private fun font(name: String): Font {
        val client = client as MinecraftAccessor
        val fontManager = client.fontManager as IModernFontManager
        val fontSet = fontManager.`ultimate$fontSetFromIdentifier`(name)
        return fromFontSet(fontSet)
    }

    fun text(font: String, text: String, x: Float, y: Float, color: Int, size: Float = 8.0f, shadow: Boolean = false) {
        val poseStack = guiGraphics.pose()
        val font = font(font)
        poseStack.pushMatrix()

        poseStack.translate(x, y)
        val fontSize = size / client.font.lineHeight
        poseStack.scale(fontSize, fontSize)

        // 描画（座標は0, 0でOK）
        guiGraphics.drawString(font, text, 0, 0, color, shadow)

        poseStack.popMatrix()
    }

    fun textCentered(
        font: String,
        text: String,
        x: Float,
        y: Float,
        color: Int,
        size: Float = 8.0f,
        shadow: Boolean = false,
    ) {
        val fontStr = font
        val font = font(fontStr)
        val scale = size / client.font.lineHeight
        val width = font.width(text) * scale
        text(fontStr, text, x - width / 2, y - size / 2, color, size, shadow)
    }
}
