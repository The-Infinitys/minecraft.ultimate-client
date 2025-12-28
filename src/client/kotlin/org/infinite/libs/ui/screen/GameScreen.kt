package org.infinite.libs.ui.screen

import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.network.chat.Component
import org.infinite.InfiniteClient
import org.infinite.libs.graphics.Graphics2D
import org.infinite.libs.graphics.graphics2d.RenderSystem2D
import org.infinite.libs.graphics.graphics2d.structs.RenderCommand2D
import org.infinite.libs.ui.widgets.LocalCategoryWidget
import org.lwjgl.glfw.GLFW
import kotlin.math.*

class GameScreen : Screen(Component.literal("Infinite Client")) {
    private var _pageIndex: Int = 0
    private val pageSize get() = InfiniteClient.localFeatures.categories.size

    private var animatedIndex: Float = 0f
    private val lerpFactor = 0.5f

    // 回転の設定
    private val radius: Float
        get() = categoryWidgets.size * 100f

    private val categoryWidgets = mutableListOf<LocalCategoryWidget>()

    var pageIndex: Int
        get() = _pageIndex
        set(value) {
            _pageIndex = if (pageSize == 0) 0 else (value % pageSize + pageSize) % pageSize
        }

    override fun init() {
        super.init()
        categoryWidgets.clear()

        val categories = InfiniteClient.localFeatures.categories
        categories.entries.withIndex().forEach { (index, entry) ->
            // ウィジェットのサイズ設定（縦長: 120x180 等）
            val widget = LocalCategoryWidget(
                0,
                0,
                120,
                180,
                entry.value,
                this,
                index,
            )
            categoryWidgets.add(widget)
            this.addRenderableWidget(widget)
        }
    }

    /**
     * @param z 奥への距離 (大きいほど奥、小さいほど手前)
     */
    data class WidgetFrameData(
        val x: Float,
        val y: Float,
        val z: Float,
        val scale: Float,
    )

    private val focusZ = 4000f
    private fun calculateWidgetFrame(index: Int): WidgetFrameData {
        if (pageSize == 0) return WidgetFrameData(0f, 0f, 0f, 1f)

        // 角度計算 (animatedIndex を引くことで回転させる)
        val angle = 2 * PI.toFloat() * (index - animatedIndex) / pageSize
        val centerZ = focusZ + radius

        val worldX = sin(angle) * radius
        val worldZ = centerZ - cos(angle) * radius

        // パースペクティブ投影のスケール計算
        // 視点(viewDistance)から見た相対的な大きさ
        val scale = focusZ / worldZ

        // 画面上の座標 (中心からのオフセット)
        val screenX = worldX / scale
        val screenY = 0f

        return WidgetFrameData(screenX, screenY, worldZ, scale)
    }

    class WidgetGraphics2D(
        deltaTracker: DeltaTracker,
        data: WidgetFrameData,
        screenWidth: Float,
        screenHeight: Float,
        widgetWidth: Float,
        widgetHeight: Float,
    ) : Graphics2D(deltaTracker) {
        override val width: Int = widgetWidth.roundToInt()
        override val height: Int = widgetHeight.roundToInt()

        init {
            this.save()
            // 1. 画面中央へ移動
            this.translate(screenWidth / 2f, screenHeight / 2f)
            // 2. 投影されたX, Yへ移動
            this.translate(data.x, data.y)
            // 3. スケール適用 (奥行きによる大きさの変化)
            this.scale(data.scale, data.scale)
            // 4. ウィジェット自体の中心を合わせるためのオフセット
            this.translate(-widgetWidth / 2f, -widgetHeight / 2f)
        }

        override fun commands(): List<RenderCommand2D> {
            this.restore()
            return super.commands()
        }
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        val target = pageIndex.toFloat()
        var diff = target - animatedIndex
        if (diff > pageSize / 2f) diff -= pageSize
        if (diff < -pageSize / 2f) diff += pageSize
        if (abs(diff) < 0.001f) {
            animatedIndex = target
        } else {
            animatedIndex += diff * lerpFactor
        }
        val renderSystem2D = RenderSystem2D(guiGraphics)
        val sw = minecraft.window.guiScaledWidth.toFloat()
        val sh = minecraft.window.guiScaledHeight.toFloat()
        val width = (sw * 0.5f).coerceAtLeast(512f).coerceAtMost(sw * 0.9f)

        val height = sh * 0.8f
        // コマンドの収集
        val bundles = categoryWidgets.map { widget ->
            val frame = calculateWidgetFrame(widget.thisIndex)
            val g2d = WidgetGraphics2D(minecraft.deltaTracker, frame, sw, sh, width, height)

            val resultG2d = widget.render(g2d)
            // (奥への距離 z, 描画コマンド)
            frame.z to resultG2d.commands()
        }

        // Zソート: Zが大きい（奥にある）順に並べる
        // sortedByDescending で奥から順にリスト化し、最後に手前を描画する
        val sortedCommands = bundles
            .sortedByDescending { it.first }
            .flatMap { it.second }

        renderSystem2D.render(sortedCommands)
    }

    override fun keyPressed(keyEvent: KeyEvent): Boolean {
        when (keyEvent.key) {
            GLFW.GLFW_KEY_RIGHT -> pageIndex++
            GLFW.GLFW_KEY_LEFT -> pageIndex--
            else -> return super.keyPressed(keyEvent)
        }
        return true
    }
}
