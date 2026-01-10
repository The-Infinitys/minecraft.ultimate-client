package org.infinite.libs.ui.widgets

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractContainerWidget
import net.minecraft.client.gui.components.Renderable
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import org.infinite.InfiniteClient
import org.infinite.libs.core.features.Property
import org.infinite.libs.graphics.bundle.Graphics2DRenderer

open class PropertyWidget<T : Property<*>>(
    x: Int,
    y: Int,
    width: Int,
    height: Int = DEFAULT_WIDGET_HEIGHT,
    protected val property: T,
) :
    AbstractContainerWidget(x, y, width, height, Component.literal("")), Renderable {
    companion object {
        protected const val DEFAULT_WIDGET_HEIGHT = 20
    }

    override fun contentHeight(): Int = this.height

    override fun scrollRate(): Double = 10.0

    override fun children(): List<GuiEventListener> =
        listOf()

    override fun setWidth(i: Int) {
        super.setWidth(i)
        relocate()
    }

    override fun setHeight(i: Int) {
        super.setHeight(i)
        relocate()
    }

    override fun setX(i: Int) {
        super.setX(i)
        relocate()
    }

    override fun setY(i: Int) {
        super.setY(i)
        relocate()
    }

    override fun setSize(i: Int, j: Int) {
        super.setSize(i, j)
        relocate()
    }

    override fun setPosition(i: Int, j: Int) {
        super.setPosition(i, j)
        relocate()
    }

    override fun setRectangle(i: Int, j: Int, k: Int, l: Int) {
        super.setRectangle(i, j, k, l)
        relocate()
    }

    protected open fun relocate() {}
    override fun mouseScrolled(d: Double, e: Double, f: Double, g: Double): Boolean {
        children().forEach { it.mouseScrolled(d, e, f, g) }
        return super.mouseScrolled(d, e, f, g)
    }

    override fun mouseMoved(d: Double, e: Double) {
        children().forEach { it.mouseMoved(d, e) }
        super.mouseMoved(d, e)
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent, bl: Boolean): Boolean {
        children().forEach { it.mouseClicked(mouseButtonEvent, bl) }
        return super.mouseClicked(mouseButtonEvent, bl)
    }

    override fun mouseDragged(mouseButtonEvent: MouseButtonEvent, d: Double, e: Double): Boolean {
        children().forEach { it.mouseDragged(mouseButtonEvent, d, e) }
        return super.mouseDragged(mouseButtonEvent, d, e)
    }

    override fun charTyped(characterEvent: CharacterEvent): Boolean {
        children().forEach { it.charTyped(characterEvent) }
        return super.charTyped(characterEvent)
    }

    override fun renderWidget(
        guiGraphics: GuiGraphics,
        i: Int,
        j: Int,
        f: Float,
    ) {
        val g2d = Graphics2DRenderer(guiGraphics)
        val colorScheme = InfiniteClient.theme.colorScheme
        val name = property.name
        val translationKey = property.translationKey() ?: "unknown"
        val description = Component.translatable(translationKey).string
        g2d.textStyle.size = 10f
        g2d.textStyle.shadow = false
        g2d.textStyle.font = "infinite_regular"
        g2d.fillStyle = colorScheme.foregroundColor
        g2d.text(name, x, y)
        g2d.textStyle.size = 8f
        g2d.fillStyle = colorScheme.secondaryColor
        g2d.text(description, x.toFloat(), y + height - g2d.textStyle.size)
        g2d.flush()
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput)
    }
}
