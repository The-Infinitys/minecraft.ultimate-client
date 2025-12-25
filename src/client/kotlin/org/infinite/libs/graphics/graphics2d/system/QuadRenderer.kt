package org.infinite.libs.graphics.graphics2d.system

import net.minecraft.client.gui.GuiGraphics
import org.infinite.libs.graphics.graphics2d.minecraft.fillQuad
import org.infinite.libs.graphics.graphics2d.system.PointPair.Companion.calculateOffsets

class QuadRenderer(
    private val guiGraphics: GuiGraphics,
) {
    fun fillQuad(
        x0: Float,
        y0: Float,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        x3: Float,
        y3: Float,
        col0: Int,
        col1: Int,
        col2: Int,
        col3: Int,
    ) {
        guiGraphics.fillQuad(x0, y0, x1, y1, x2, y2, x3, y3, col0, col1, col2, col3)
    }

    fun strokeQuad(
        x0: Float,
        y0: Float,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        x3: Float,
        y3: Float,
        color: Int,
        strokeWidth: Float,
    ) {
        val halfWidth = strokeWidth / 2f

        val calcCorner = { tx: Float, ty: Float, px: Float, py: Float, nx: Float, ny: Float ->
            // cx, cy を隣接点の中点として算出
            val cx = (tx + px + nx) / 3f
            val cy = (ty + py + ny) / 3f
            calculateOffsets(tx, ty, cx, cy, halfWidth)
        }

        // 2. 各頂点の PointPair (内側・外側の座標セット) を取得
        val p0 = calcCorner(x0, y0, x3, y3, x1, y1) // 頂点0 (隣接: 3, 1)
        val p1 = calcCorner(x1, y1, x0, y0, x2, y2) // 頂点1 (隣接: 0, 2)
        val p2 = calcCorner(x2, y2, x1, y1, x3, y3) // 頂点2 (隣接: 1, 3)
        val p3 = calcCorner(x3, y3, x2, y2, x0, y0) // 頂点3 (隣接: 2, 0)

        // 3. 4つの「辺」をそれぞれ描画
        // 各辺は、隣り合う頂点の「外側(ox, oy)」と「内側(ix, iy)」の計4点で構成される矩形

        // 辺 0-1
        fillQuad(p0.ox, p0.oy, p0.ix, p0.iy, p1.ix, p1.iy, p1.ox, p1.oy, color)
        // 辺 1-2
        fillQuad(p1.ox, p1.oy, p1.ix, p1.iy, p2.ix, p2.iy, p2.ox, p2.oy, color)
        // 辺 2-3
        fillQuad(p2.ox, p2.oy, p2.ix, p2.iy, p3.ix, p3.iy, p3.ox, p3.oy, color)
        // 辺 3-0
        fillQuad(p3.ox, p3.oy, p3.ix, p3.iy, p0.ix, p0.iy, p0.ox, p0.oy, color)
    }

    private fun fillQuad(
        x0: Float,
        y0: Float,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        x3: Float,
        y3: Float,
        color: Int,
    ) {
        fillQuad(x0, y0, x1, y1, x2, y2, x3, y3, color, color, color, color)
    }
}
