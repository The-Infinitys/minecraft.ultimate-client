package org.infinite.libs.ui.widgets

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.input.KeyEvent
import net.minecraft.network.chat.Component
import org.infinite.InfiniteClient
import org.infinite.utils.mix
import org.lwjgl.glfw.GLFW

/**
 * サジェスト機能付きのEditBox
 */
open class SuggestInputWidget(
    x: Int,
    y: Int,
    width: Int,
    initialValue: String,
    val suggestions: () -> List<String>,
    val onComplete: (String?) -> Unit,
) : EditBox(Minecraft.getInstance().font, x, y, width, 20, Component.literal("")) {

    private var filteredSuggestions = mutableListOf<String>()
    private var selectedIndex = -1 // -1 はサジェスト未選択（入力値を使用）

    init {
        this.value = initialValue
        this.setResponder { updateSuggestions(it) }
        this.isFocused = true
    }

    private fun updateSuggestions(input: String) {
        val all = suggestions()
        filteredSuggestions = if (input.isEmpty()) {
            mutableListOf()
        } else {
            // フィルタリング
            all.filter { it.contains(input, ignoreCase = true) }.take(5).toMutableList()
        }
        // 候補が変わったら選択状態をリセット
        selectedIndex = -1
    }

    override fun keyPressed(keyEvent: KeyEvent): Boolean {
        val keyCode = keyEvent.key

        // --- Tab / Shift+Tab による候補選択ロジック ---
        if (keyCode == GLFW.GLFW_KEY_TAB && filteredSuggestions.isNotEmpty()) {
            val isShiftDown = keyEvent.modifiers and GLFW.GLFW_MOD_SHIFT != 0

            if (isShiftDown) {
                // 前の候補へ
                selectedIndex--
                if (selectedIndex < -1) selectedIndex = filteredSuggestions.size - 1
            } else {
                // 次の候補へ
                selectedIndex++
                if (selectedIndex >= filteredSuggestions.size) selectedIndex = -1
            }

            // 選択中のテキストを EditBox に仮反映させる（必要に応じて）
            // if (selectedIndex != -1) this.value = filteredSuggestions[selectedIndex]

            return true
        }

        // --- 確定とキャンセル ---
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            val finalValue = if (selectedIndex != -1) filteredSuggestions[selectedIndex] else this.value
            if (finalValue.isNotBlank()) onComplete(finalValue)
            return true
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            onComplete(null)
            return true
        }

        return super.keyPressed(keyEvent)
    }

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        super.renderWidget(guiGraphics, mouseX, mouseY, delta)

        if (isFocused && filteredSuggestions.isNotEmpty()) {
            val colorScheme = InfiniteClient.theme.colorScheme
            val font = Minecraft.getInstance().font
            var suggestY = y + height + 2

            // サジェストボックス全体の描画
            for (index in filteredSuggestions.indices) {
                val suggestion = filteredSuggestions[index]
                val isSelected = index == selectedIndex

                // 背景: 選択中はアクセントカラーを混ぜてハイライト
                val bgColor = if (isSelected) {
                    colorScheme.backgroundColor.mix(colorScheme.accentColor, 0.4f)
                } else {
                    colorScheme.backgroundColor
                }

                guiGraphics.fill(x, suggestY, x + width, suggestY + 12, bgColor)

                // 枠線（選択中のみ）
                if (isSelected) {
                    guiGraphics.fill(x, suggestY, x + 1, suggestY + 12, colorScheme.accentColor)
                }

                // テキスト
                val textColor = if (isSelected) colorScheme.foregroundColor else colorScheme.secondaryColor
                guiGraphics.drawString(font, suggestion, x + 4, suggestY + 2, textColor, false)

                suggestY += 12
            }
        }
    }
}
