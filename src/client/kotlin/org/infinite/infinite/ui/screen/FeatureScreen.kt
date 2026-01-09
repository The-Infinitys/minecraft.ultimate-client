package org.infinite.infinite.ui.screen

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.infinite.InfiniteClient
import org.infinite.libs.core.features.Feature
import org.infinite.libs.ui.layout.ScrollableLayoutContainer

class FeatureScreen<T : Feature>(
    private val feature: T,
    private val parent: Screen,
) : Screen(Component.translatable(feature.translation())) {

    private lateinit var container: ScrollableLayoutContainer

    // レイアウト定数
    private val headerHeight = 60
    private val margin = 10

    override fun init() {
        super.init()

        val innerWidth = width - (margin * 2)

        // 1. 内部レイアウト（LinearLayout）の構築
        val innerLayout = LinearLayout.vertical().spacing(8)

        // Featureが持つ全プロパティをスキャンしてウィジェット化
        // 実際の運用では Factory クラスに切り出すのが理想的です
//        feature.properties.forEach { (name, prop) ->
//            val widget = createPropertyWidget(prop, innerWidth)
//            innerLayout.addChild(widget)
//        }

        // レイアウトの計算実行
        innerLayout.arrangeElements()

        // 2. スクロールコンテナの初期化
        // i (高さ) はコンテンツを表示できる最大範囲を指定
        container = ScrollableLayoutContainer(minecraft, innerLayout, innerWidth).apply {
            this.x = margin
            this.y = headerHeight
            this.setMinWidth(innerWidth)
            this.setMaxHeight(height - headerHeight - margin)
        }
        this.addRenderableWidget(container)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        // 背景とヘッダーの描画
        renderBackground(guiGraphics, mouseX, mouseY, delta)

        val centerX = width / 2f
        val colorScheme = InfiniteClient.theme.colorScheme

        // タイトル
        guiGraphics.drawCenteredString(font, title, centerX.toInt(), 15, colorScheme.foregroundColor)

        // 説明文 (あれば)
        val descKey = "${feature.translation()}.desc"
        val desc = Component.translatable(descKey)
        val splitDesc = font.split(desc, (width * 0.8f).toInt())
        var currentY = 30
        splitDesc.forEach { line ->
            guiGraphics.drawCenteredString(font, line, centerX.toInt(), currentY, 0xAAAAAA)
            currentY += font.lineHeight
        }

        // 区切り線
        guiGraphics.fill(margin, headerHeight - 2, width - margin, headerHeight - 1, 0x44FFFFFF)

        super.render(guiGraphics, mouseX, mouseY, delta)
    }

    override fun onClose() {
        minecraft.setScreen(parent)
    }
}
