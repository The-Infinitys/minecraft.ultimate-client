package org.infinite.libs.ui.screen

import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import org.infinite.libs.graphics.Graphics2D
import org.infinite.libs.graphics.graphics2d.RenderSystem2D
import org.infinite.libs.graphics.graphics2d.structs.RenderCommand2D
import org.infinite.libs.ui.widgets.AbstractCarouselWidget
import org.lwjgl.glfw.GLFW
import kotlin.math.*

abstract class AbstractCarouselScreen<T>(title: Component) : Screen(title) {
    val currentWidget: AbstractCarouselWidget<T>
        get() = carouselWidgets[pageIndex]
    private var _pageIndex: Int = 0
    protected abstract val dataSource: List<T>
    protected val pageSize get() = dataSource.size

    private val screenWidth: Float
        get() = minecraft.window.guiScaledWidth.toFloat()
    val screenHeight: Float
        get() = minecraft.window.guiScaledHeight.toFloat()
    private val widgetWidth: Float
        get() = (screenWidth * 0.5f).coerceAtLeast(512f).coerceAtMost(screenWidth * 0.9f)
    private val widgetHeight: Float
        get() = screenHeight * 0.8f
    private var animatedIndex: Float = 0f
    protected open val lerpFactor = 0.5f

    protected open val radius: Float get() = carouselWidgets.size * 100f
    protected val carouselWidgets = mutableListOf<AbstractCarouselWidget<T>>()

    var pageIndex: Int
        get() = _pageIndex
        set(value) {
            _pageIndex = if (pageSize == 0) 0 else (value % pageSize + pageSize) % pageSize
        }

    /**
     * 具象クラスで、DataSourceに基づいたWidgetのインスタンスを作成して返してください。
     */
    abstract fun createWidget(index: Int, data: T): AbstractCarouselWidget<T>

    override fun init() {
        super.init()
        carouselWidgets.clear()

        dataSource.withIndex().forEach { (index, data) ->
            val widget = createWidget(index, data)
            carouselWidgets.add(widget)
            this.addRenderableWidget(widget)
        }
    }

    data class WidgetFrameData(
        val x: Float,
        val y: Float,
        val z: Float,
        val scale: Float,
        val widgetWidth: Float,
        val widgetHeight: Float,
    )

    private val focusZ = 4000f
    private fun calculateWidgetFrame(index: Int): WidgetFrameData {
        if (pageSize == 0) return WidgetFrameData(0f, 0f, 0f, 1f, 0f, 0f)

        val angle = 2 * PI.toFloat() * (index - animatedIndex) / pageSize
        val centerZ = focusZ + radius
        val worldX = sin(angle) * radius
        val worldZ = centerZ - cos(angle) * radius
        val scale = focusZ / worldZ
        val screenX = worldX / scale

        return WidgetFrameData(screenX, 0f, worldZ, scale, widgetWidth, widgetHeight)
    }

    class WidgetGraphics2D(
        deltaTracker: DeltaTracker,
        data: WidgetFrameData,
        screenWidth: Float,
        screenHeight: Float,
    ) : Graphics2D(deltaTracker) {
        override val width: Int = data.widgetWidth.roundToInt()
        override val height: Int = data.widgetHeight.roundToInt()

        init {
            this.save()
            this.translate(screenWidth / 2f, screenHeight / 2f)
            this.translate(data.x, data.y)
            this.scale(data.scale, data.scale)
            this.translate(-data.widgetWidth / 2f, -data.widgetHeight / 2f)
        }

        override fun commands(): List<RenderCommand2D> {
            this.restore()
            return super.commands()
        }
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        // アニメーション更新
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
        // 基準となるウィジェットのサイズ

        val bundles = carouselWidgets.map { widget ->
            val frame = calculateWidgetFrame(widget.thisIndex)
            widget.widgetFrameData = frame
            // --- 当たり判定の更新処理を追加 ---
            // 3D空間の座標をスクリーン座標に変換し、ウィジェットのプロパティに適用
            val scaledWidth = (widgetWidth * frame.scale).toInt()
            val scaledHeight = (widgetHeight * frame.scale).toInt()

            // Graphics2Dでのtranslate(sw/2, sh/2) + translate(frame.x, frame.y) に合わせる
            // 中央基準から左上基準に変換
            widget.x = (screenWidth / 2f + frame.x - scaledWidth / 2f).toInt()
            widget.y = (screenHeight / 2f + frame.y - scaledHeight / 2f).toInt()
            widget.width = scaledWidth
            widget.height = scaledHeight
            // ------------------------------

            val g2d = WidgetGraphics2D(minecraft.deltaTracker, frame, screenWidth, screenHeight)
            val resultG2d = widget.render(g2d)
            frame.z to resultG2d.commands()
        }
        // 重なり順（Zオーダー）を考慮して描画
        val sortedCommands = bundles.sortedByDescending { it.first }.flatMap { it.second }
        renderSystem2D.render(sortedCommands)
        carouselWidgets[pageIndex].renderWidget(guiGraphics, mouseX, mouseY, delta)
    }

    override fun keyPressed(keyEvent: KeyEvent): Boolean {
        when (keyEvent.key) {
            GLFW.GLFW_KEY_RIGHT -> pageIndex++
            GLFW.GLFW_KEY_LEFT -> pageIndex--
            else -> return super.keyPressed(keyEvent)
        }
        return true
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent, bl: Boolean): Boolean {
        // Z値が手前（frame.z が小さい）のものから順にクリック判定を行う
        val sortedWidgets = carouselWidgets.sortedBy { calculateWidgetFrame(it.thisIndex).z }
        for (widget in sortedWidgets) {
            if (widget.mouseClicked(mouseButtonEvent, bl)) {
                this.focused = widget
                return true
            }
        }
        return super.mouseClicked(mouseButtonEvent, bl)
    }
}
