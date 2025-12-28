package org.infinite.libs.graphics.graphics2d.system

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import org.infinite.libs.graphics.graphics2d.structs.RenderCommand2D

class ItemRenderer(private val gui: GuiGraphics) {
    private val client = Minecraft.getInstance()

    fun drawItem(cmd: RenderCommand2D.DrawItem) {
        val stack = cmd.stack
        if (stack.isEmpty) return

        val x = cmd.x
        val y = cmd.y
        val scale = cmd.scale

        val pose = gui.pose()

        pose.pushMatrix()

        // 1. 位置の移動
        pose.translate(x, y)

        if (scale != 1f) {
            pose.scale(scale, scale)
        }

        // 3. アイテム本体の描画
        // renderItemは現在のPoseStack（行列）を考慮して描画します
        gui.renderItem(stack, 0, 0)

        // 4. 装飾（耐久値バー、スタック数）の描画
        // 装飾も行列の影響を受けるため、位置は 0, 0 で指定します
        gui.renderItemDecorations(client.font, stack, 0, 0)

        pose.popMatrix()
    }
}
