package org.infinite.libs.ui.widgets

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import org.infinite.InfiniteClient
import org.infinite.libs.core.features.categories.category.LocalCategory
import org.infinite.libs.ui.screen.GameScreen

class LocalCategoryWidget(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    private val localCategory: LocalCategory,
    private val parent: GameScreen,
    val thisIndex: Int,
) : AbstractWidget(x, y, width, height, Component.translatable(localCategory.translation())) {

    private val spawnTime = System.currentTimeMillis()
    private val animationDuration = 250L
    private val targetAlpha = 0.5f
    fun render(graphics2D: GameScreen.WidgetGraphics2D): GameScreen.WidgetGraphics2D {
        val theme = InfiniteClient.theme
        val progress = (System.currentTimeMillis() - spawnTime).toFloat() / animationDuration * targetAlpha
        val alpha = progress.coerceIn(0f, targetAlpha)
        theme.renderBackGround(0f, 0f, graphics2D.width.toFloat(), graphics2D.height.toFloat(), graphics2D, alpha)
        return graphics2D
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput)
    }

    override fun renderWidget(
        guiGraphics: GuiGraphics,
        i: Int,
        j: Int,
        f: Float,
    ) {
    }

    // 標準的な onClick オーバーライド
    override fun onClick(mouseButtonEvent: MouseButtonEvent, bl: Boolean) {
        println("Selected category: ${localCategory.translation()}")
        parent.pageIndex = thisIndex
    }
}
