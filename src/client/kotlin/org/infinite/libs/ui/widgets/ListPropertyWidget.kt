package org.infinite.libs.ui.widgets

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import org.infinite.InfiniteClient
import org.infinite.libs.core.features.property.ListProperty
import org.infinite.libs.graphics.bundle.Graphics2DRenderer
import org.infinite.utils.mix

class ListPropertyWidget<T : Any>(
    x: Int,
    y: Int,
    width: Int,
    property: ListProperty<T>,
) : PropertyWidget<ListProperty<T>>(
    x,
    y,
    width,
    DEFAULT_WIDGET_HEIGHT * 10,
    property,
) {
    private var activeInputWidget: AbstractWidget? = null
    private var editingIndex: Int = -1
    override fun relocate() {
        super.relocate()
        activeInputWidget?.let {
            it.x = this.x + 4
            it.width = this.width - 32 // バツボタンと被らない幅
        }
    }
    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent, bl: Boolean): Boolean {
        // 1. 入力中ならそちらのイベントを最優先
        if (activeInputWidget != null) {
            if (activeInputWidget!!.mouseClicked(mouseButtonEvent, bl)) return true
            // 入力欄の外をクリックしたら閉じる
            activeInputWidget = null
            editingIndex = -1
            return true
        }

        val mouseX = mouseButtonEvent.x
        val mouseY = mouseButtonEvent.y
        val itemHeight = 22f
        var currentY = y + DEFAULT_WIDGET_HEIGHT.toFloat()

        // 2. リスト要素のループ
        property.value.forEachIndexed { index, _ ->
            // 削除ボタン判定
            val btnSize = 14f
            val bx = x + width - btnSize - 5f
            val by = currentY + (itemHeight - btnSize) / 2f

            if (mouseX >= bx && mouseX <= bx + btnSize && mouseY >= by && mouseY <= by + btnSize) {
                property.removeAt(index)
                relocate() // 高さを再計算
                return true
            }

            // 編集行判定
            if (mouseX >= x && mouseX <= x + width - 20 && mouseY >= currentY && mouseY <= currentY + itemHeight) {
                startEditing(index, property.value[index], currentY.toInt())
                return true
            }
            currentY += itemHeight
        }

        // 3. 追加ボタン判定
        if (mouseX >= x && mouseX <= x + width && mouseY >= currentY && mouseY <= currentY + 22) {
            startEditing(-1, null, currentY.toInt())
            return true
        }

        return super.mouseClicked(mouseButtonEvent, bl)
    }

    private fun startEditing(index: Int, item: T?, atY: Int) {
        editingIndex = index
        activeInputWidget = property.createInputWidget(x + 4, atY + 1, width - 8, item) { newValue ->
            if (newValue != null) {
                if (editingIndex == -1) property.add(newValue) else property.replaceAt(editingIndex, newValue)
            }
            activeInputWidget = null
            editingIndex = -1
            relocate() // 変更後に高さを更新
        }
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        super.renderWidget(guiGraphics, i, j, f)
        val g2d = Graphics2DRenderer(guiGraphics)
        val theme = InfiniteClient.theme
        val colorScheme = theme.colorScheme

        var currentY = y + DEFAULT_WIDGET_HEIGHT.toFloat()
        val itemHeight = 22f

        property.value.forEachIndexed { index, item ->
            if (index == editingIndex) {
                activeInputWidget?.y = currentY.toInt()
                currentY += itemHeight
                return@forEachIndexed
            }

            val isHover = i >= x && i <= x + width && j >= currentY && j <= currentY + itemHeight
            if (isHover) {
                g2d.fillStyle = colorScheme.secondaryColor.mix(colorScheme.backgroundColor, 0.4f)
                g2d.fillRect(x.toFloat(), currentY, width.toFloat(), itemHeight)
            }

            // アイテム描画
            property.renderElement(g2d, item, x + 4, currentY.toInt() + 2, width - 35, itemHeight.toInt() - 4)

            // バツボタン描画 (修正版)
            val btnSize = 12f
            val bx = x + width - btnSize - 6f
            val by = currentY + (itemHeight - btnSize) / 2f
            val isHoverDel = i >= bx && i <= bx + btnSize && j >= by && j <= by + btnSize

            g2d.fillStyle = if (isHoverDel) colorScheme.accentColor else colorScheme.secondaryColor
            g2d.fillRect(bx, by, btnSize, btnSize)

            g2d.beginPath()
            g2d.strokeStyle.color = colorScheme.foregroundColor
            g2d.strokeStyle.width = 1.0f
            val p = 3f // 内側パディング
            g2d.moveTo(bx + p, by + p)
            g2d.lineTo(bx + btnSize - p, by + btnSize - p)
            g2d.strokePath()
            g2d.moveTo(bx + btnSize - p, by + p)
            g2d.lineTo(bx + p, by + btnSize - p)
            g2d.strokePath()

            currentY += itemHeight
        }

        // 追加ボタン
        if (activeInputWidget == null || editingIndex != -1) {
            val isHoverAdd = i >= x && i <= x + width && j >= currentY && j <= currentY + 22
            val buttonHeight = 22f
            val opacity = if (isHoverAdd) 1f else 0.5f
            theme.renderBackGround(x.toFloat(), currentY, width.toFloat(), buttonHeight, g2d, opacity)
            g2d.fillStyle = colorScheme.foregroundColor
            g2d.textCentered("+ Add Item", x + width / 2f, currentY + buttonHeight / 2f)
        }

        g2d.flush()
        activeInputWidget?.render(guiGraphics, i, j, f)
    }

    // キーボードイベントの伝搬
    override fun keyPressed(keyEvent: KeyEvent): Boolean {
        return activeInputWidget?.keyPressed(keyEvent) ?: super.keyPressed(keyEvent)
    }

    override fun charTyped(characterEvent: CharacterEvent): Boolean {
        return activeInputWidget?.charTyped(characterEvent) ?: super.charTyped(characterEvent)
    }

    override fun mouseMoved(d: Double, e: Double) {
        super.mouseMoved(d, e)
        activeInputWidget?.mouseMoved(d, e)
    }

    override fun mouseScrolled(d: Double, e: Double, f: Double, g: Double): Boolean {
        return activeInputWidget?.mouseScrolled(d, e, f, g) ?: super.mouseScrolled(d, e, f, g)
    }

    override fun mouseDragged(mouseButtonEvent: MouseButtonEvent, d: Double, e: Double): Boolean {
        return activeInputWidget?.mouseDragged(mouseButtonEvent, d, e) ?: super.mouseDragged(mouseButtonEvent, d, e)
    }

    override fun mouseReleased(mouseButtonEvent: MouseButtonEvent): Boolean {
        return activeInputWidget?.mouseReleased(mouseButtonEvent) ?: super.mouseReleased(mouseButtonEvent)
    }
}
