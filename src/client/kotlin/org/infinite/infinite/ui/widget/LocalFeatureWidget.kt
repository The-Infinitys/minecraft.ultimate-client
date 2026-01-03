package org.infinite.infinite.ui.widget

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractContainerWidget
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import org.infinite.InfiniteClient
import org.infinite.libs.core.features.feature.LocalFeature
import org.infinite.libs.graphics.bundle.Graphics2DRenderer

private const val padding = 4
private const val fontSize = 12
class LocalFeatureWidget(
    x: Int,
    y: Int,
    width: Int,
    height: Int = fontSize + padding * 2,
    private val feature: LocalFeature,
) :
    AbstractContainerWidget(
        x,
        y,
        width,
        height,
        Component.literal(feature.name),
    ) {

    //    private val components: WidgetComponents
    private val minecraft: Minecraft
        get() = Minecraft.getInstance()

    private data class WidgetComponents(
        val titleComponent: StringWidget,
    )

    override fun contentHeight(): Int = height

    override fun scrollRate(): Double = 10.0

    override fun children(): List<GuiEventListener> = listOf()

    override fun mouseScrolled(d: Double, e: Double, f: Double, g: Double): Boolean {
        return super.mouseScrolled(d, e, f, g)
    }

    override fun renderWidget(
        guiGraphics: GuiGraphics,
        i: Int,
        j: Int,
        f: Float,
    ) {
        val theme = InfiniteClient.theme
        val graphics2DRenderer = Graphics2DRenderer(guiGraphics)
        theme.renderBackGround(this.x, this.y, this.width, this.height, graphics2DRenderer, 0.8f)
        val paddingF = padding.toFloat()
        val colorScheme = theme.colorScheme
        graphics2DRenderer.textStyle.font = "infinite_regular"
        graphics2DRenderer.fillStyle = colorScheme.foregroundColor
        graphics2DRenderer.textStyle.size = fontSize.toFloat()
        graphics2DRenderer.text(feature.name, this.x + paddingF, this.y + paddingF)
        graphics2DRenderer.flush()
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput)
    }
}
