package org.infinite.infinite.ui.widget

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import org.infinite.InfiniteClient
import org.infinite.libs.core.features.Feature
import org.infinite.libs.graphics.Graphics2D
import org.infinite.libs.graphics.bundle.Graphics2DRenderer
import org.infinite.libs.log.LogSystem

class FeatureResetButton(x: Int, y: Int, width: Int, height: Int, feature: Feature) :
    Button(
        x,
        y,
        width,
        height,
        Component.literal("Reset"),
        {
            LogSystem.log("RESET")
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

    fun render(
        graphics2D: Graphics2D,
    ) {
        val theme = InfiniteClient.theme
        val colorScheme = theme.colorScheme
        theme.renderBackGround(this.x, this.y, this.width, this.height, graphics2D, 0.8f)
        graphics2D.strokeStyle.width = 2f
        graphics2D.strokeStyle.color = colorScheme.redColor
        graphics2D.fillStyle = colorScheme.redColor
        graphics2D.strokeRect(this.x, this.y, this.width, this.height)
        graphics2D.text("HELLO", this.x, this.y)
    }
}
