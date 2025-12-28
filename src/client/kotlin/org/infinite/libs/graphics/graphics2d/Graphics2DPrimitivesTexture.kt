package org.infinite.libs.graphics.graphics2d

import net.minecraft.client.Minecraft
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import org.infinite.libs.graphics.graphics2d.structs.RenderCommand2D
import org.joml.Matrix3x2f
import java.util.*

class Graphics2DPrimitivesTexture(
    private val commandQueue: LinkedList<RenderCommand2D>,
    private val transformProvider: () -> Matrix3x2f,
) {
    private val client = Minecraft.getInstance()

    /**
     * アイテム描画をトランスフォームと同期してコマンド化します。
     * @param stack アイテム
     * @param x 基本X座標
     * @param y 基本Y座標
     * @param size 描画サイズ（デフォルト16f）。内部で scale に変換されます。
     */
    fun drawItem(stack: ItemStack, x: Float, y: Float, size: Float = 16f) {
        if (stack.isEmpty) return
        commandQueue.add(RenderCommand2D.DrawItem(stack, x, y, size / 16f))
    }

    // 汎用テクスチャ描画
    fun drawTexture(
        identifier: Identifier,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        u: Float,
        v: Float,
        uWidth: Float,
        vHeight: Float,
        textureWidth: Float,
        textureHeight: Float,
        color: Int,
    ) {
        commandQueue.add(
            RenderCommand2D.DrawTexture(
                identifier, x, y, width, height, u, v, uWidth, vHeight, textureWidth, textureHeight, color,
            ),
        )
    }
}
