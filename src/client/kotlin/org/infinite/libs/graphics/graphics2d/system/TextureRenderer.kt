package org.infinite.libs.graphics.graphics2d.system

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderPipelines
import org.infinite.libs.graphics.graphics2d.structs.RenderCommand2D

class TextureRenderer(private val gui: GuiGraphics) {
    fun drawTexture(cmd: RenderCommand2D.DrawTexture) {
        // ARGBから各成分を抽出して適用（必要に応じてgui.setColorを使用）
        val a = (cmd.color shr 24 and 0xFF) / 255f
        val r = (cmd.color shr 16 and 0xFF) / 255f
        val g = (cmd.color shr 8 and 0xFF) / 255f
        val b = (cmd.color and 0xFF) / 255f

        // 頂点カラーを適用するためにRenderSystemのステートをセット
        // (注: 1.21+のblitの実装に合わせる)
        gui.blit(
            RenderPipelines.GUI_TEXTURED,
            cmd.identifier,
            cmd.x.toInt(),
            cmd.y.toInt(),
            cmd.u,
            cmd.v,
            cmd.width.toInt(),
            cmd.height.toInt(),
            cmd.textureWidth.toInt(),
            cmd.textureHeight.toInt(),
        )
    }
}
