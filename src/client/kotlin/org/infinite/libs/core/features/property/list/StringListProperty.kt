package org.infinite.libs.core.features.property.list

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.input.KeyEvent
import net.minecraft.network.chat.Component
import org.infinite.InfiniteClient
import org.infinite.libs.core.features.property.ListProperty
import org.infinite.libs.graphics.Graphics2D
import org.lwjgl.glfw.GLFW

open class StringListProperty(default: List<String>) : ListProperty<String>(default) {

    override fun convertElement(anyValue: Any): String? = anyValue.toString()

    override fun renderElement(graphics2D: Graphics2D, item: String, x: Int, y: Int, width: Int, height: Int) {
        graphics2D.fillStyle = InfiniteClient.theme.colorScheme.foregroundColor
        graphics2D.textStyle.font = "infinite_regular"
        graphics2D.textStyle.size = 12f
        graphics2D.textStyle.shadow = true
        graphics2D.text(item, x, y + (height - 8) / 2)
    }

    override fun createInputWidget(
        x: Int,
        y: Int,
        width: Int,
        initialValue: String?,
        onComplete: (String?) -> Unit,
    ): AbstractWidget {
        val editBox = object : EditBox(Minecraft.getInstance().font, x, y, width, 20, Component.literal("")) {
            override fun keyPressed(keyEvent: KeyEvent): Boolean {
                val keyCode = keyEvent.key
                if (keyCode == GLFW.GLFW_KEY_ENTER) {
                    if (this.value.isNotBlank()) onComplete(this.value)
                    return true
                }
                if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                    onComplete(null)
                    return true
                }
                return super.keyPressed(keyEvent)
            }
        }.apply {
            // ここで初期値をセットする
            this.value = initialValue ?: ""
            this.isFocused = true
            this.cursorPosition = this.value.length
        }
        return editBox
    }
}
