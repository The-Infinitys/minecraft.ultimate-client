package org.infinite.libs.graphics.graphics2d.system

import net.minecraft.client.gui.GuiGraphics
import org.infinite.libs.graphics.graphics2d.minecraft.fill
import org.infinite.libs.graphics.graphics2d.minecraft.fillQuad
import org.infinite.libs.graphics.graphics2d.system.QuadRenderer.QuadColorSampler

class RectRenderer(
    private val guiGraphics: GuiGraphics,
) {
    // --- Fill Logic ---

    fun fillRect(x: Float, y: Float, w: Float, h: Float, color: Int) {
        guiGraphics.fill(x, y, x + w, y + h, color)
    }

    fun fillRect(x: Float, y: Float, w: Float, h: Float, col0: Int, col1: Int, col2: Int, col3: Int) {
        guiGraphics.fillQuad(
            x, y, // 左上 (0)
            x + w, y, // 右上 (1)
            x + w, y + h, // 右下 (2)
            x, y + h, // 左下 (3)
            col0, col1, col2, col3,
        )
    }

    // --- Stroke Logic ---

    fun strokeRect(
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        color: Int,
        strokeWidth: Float,
    ) {
        val hw = strokeWidth / 2f
        // 外側と内側の矩形座標を計算
        val ox0 = x - hw
        val oy0 = y - hw
        val ox1 = x + w + hw
        val oy1 = y - hw
        val ox2 = x + w + hw
        val oy2 = y + h + hw
        val ox3 = x - hw
        val oy3 = y + h + hw

        val ix0 = x + hw
        val iy0 = y + hw
        val ix1 = x + w - hw
        val iy1 = y + hw
        val ix2 = x + w - hw
        val iy2 = y + h - hw
        val ix3 = x - hw
        val iy3 = y + h - hw

        // 上・右・下・左の4つの台形を描画
        drawEdgeRaw(ox0, oy0, ox1, oy1, ix1, iy1, ix0, iy0, color, color, color, color)
        drawEdgeRaw(ox1, oy1, ox2, oy2, ix2, iy2, ix1, iy1, color, color, color, color)
        drawEdgeRaw(ox2, oy2, ox3, oy3, ix3, iy3, ix2, iy2, color, color, color, color)
        drawEdgeRaw(ox3, oy3, ox0, oy0, ix0, iy0, ix3, iy3, color, color, color, color)
    }

    /**
     * 四隅の色を指定する枠線描画
     */
    fun strokeRect(
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        col0: Int, // 左上
        col1: Int, // 右上
        col2: Int, // 右下
        col3: Int, // 左下
        strokeWidth: Float,
    ) {
        val hw = strokeWidth / 2f

        // 各頂点の PointPair (Outer と Inner) を生成
        val p0 = PointPair(x - hw, y - hw, x + hw, y + hw) // 左上
        val p1 = PointPair(x + w + hw, y - hw, x + w - hw, y + hw) // 右上
        val p2 = PointPair(x + w + hw, y + h + hw, x + w - hw, y + h - hw) // 右下
        val p3 = PointPair(x - hw, y + h + hw, x + hw, y + h - hw) // 左下

        // 内側の色をサンプリング（元の長方形のグラデーションから抽出）
        val innerCols = if (strokeWidth > 0f) {
            QuadColorSampler.sample(
                p0.ix, p0.iy, p1.ix, p1.iy, p2.ix, p2.iy, p3.ix, p3.iy,
                x, y, x + w, y, x + w, y + h, x, y + h,
                col0, col1, col2, col3,
            )
        } else {
            listOf(col0, col1, col2, col3)
        }

        // 4つのエッジを描画
        // 上辺: p0 -> p1
        drawEdgeRaw(p0.ox, p0.oy, p1.ox, p1.oy, p1.ix, p1.iy, p0.ix, p0.iy, col0, col1, innerCols[1], innerCols[0])
        // 右辺: p1 -> p2
        drawEdgeRaw(p1.ox, p1.oy, p2.ox, p2.oy, p2.ix, p2.iy, p1.ix, p1.iy, col1, col2, innerCols[2], innerCols[1])
        // 下辺: p2 -> p3
        drawEdgeRaw(p2.ox, p2.oy, p3.ox, p3.oy, p3.ix, p3.iy, p2.ix, p2.iy, col2, col3, innerCols[3], innerCols[2])
        // 左辺: p3 -> p0
        drawEdgeRaw(p3.ox, p3.oy, p0.ox, p0.oy, p0.ix, p0.iy, p3.ix, p3.iy, col3, col0, innerCols[0], innerCols[3])
    }

    private fun drawEdgeRaw(
        oxs: Float,
        oys: Float,
        oxe: Float,
        oye: Float,
        ixe: Float,
        iye: Float,
        ixs: Float,
        iys: Float,
        cOutS: Int,
        cOutE: Int,
        cInE: Int,
        cInS: Int,
    ) {
        guiGraphics.fillQuad(
            oxs, oys, oxe, oye, ixe, iye, ixs, iys,
            cOutS, cOutE, cInE, cInS,
        )
    }
}
