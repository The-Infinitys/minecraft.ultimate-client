package org.infinite.libs.graphics.graphics2d.system

import net.minecraft.client.gui.GuiGraphics
import org.infinite.libs.graphics.graphics2d.minecraft.fillQuad
import org.infinite.libs.graphics.graphics2d.minecraft.fillTriangle
import org.infinite.libs.graphics.graphics2d.system.PointPair.Companion.calculateForMiter
import kotlin.math.abs

class TriangleRenderer(
    private val guiGraphics: GuiGraphics,
) {
    /**
     * 三角形を塗りつぶす（各頂点の色を指定可能）
     */
    fun fillTriangle(
        x0: Float,
        y0: Float,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        col0: Int,
        col1: Int,
        col2: Int,
    ) {
        guiGraphics.fillTriangle(x0, y0, x1, y1, x2, y2, col0, col1, col2)
    }

    /**
     * 単一色で三角形を塗りつぶす
     */
    fun fillTriangle(
        x0: Float,
        y0: Float,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        color: Int,
    ) = fillTriangle(x0, y0, x1, y1, x2, y2, color, color, color)

    /**
     * 中心点から内外にオフセットして、三角形の枠線を描画する
     */
    fun strokeTriangle(
        x0: Float,
        y0: Float,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        color: Int,
        strokeWidth: Float,
    ) {
        val hw = strokeWidth / 2f

        // 重心は使わず、前後の頂点との関係からオフセットを計算
        // p0 に対しては、前(prev)が p2、次(next)が p1
        val p0 = calculateForMiter(x0, y0, x2, y2, x1, y1, hw)
        val p1 = calculateForMiter(x1, y1, x0, y0, x2, y2, hw)
        val p2 = calculateForMiter(x2, y2, x1, y1, x0, y0, hw)

        // 辺の描画
        drawStrokeEdge(p0, p1, color)
        drawStrokeEdge(p1, p2, color)
        drawStrokeEdge(p2, p0, color)
    }

    private fun drawStrokeEdge(start: PointPair, end: PointPair, color: Int) {
        // QuadRenderer と頂点順序を合わせておくと混乱が少ないです
        guiGraphics.fillQuad(
            start.ox, start.oy,
            end.ox, end.oy,
            end.ix, end.iy,
            start.ix, start.iy,
            color, color, color, color,
        )
    }

    fun strokeTriangle(
        x0: Float,
        y0: Float,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        col0: Int,
        col1: Int,
        col2: Int,
        strokeWidth: Float,
    ) {
        val hw = strokeWidth / 2f

        // 1. 各角のオフセット座標を計算
        val p0 = calculateForMiter(x0, y0, x2, y2, x1, y1, hw)
        val p1 = calculateForMiter(x1, y1, x0, y0, x2, y2, hw)
        val p2 = calculateForMiter(x2, y2, x1, y1, x0, y0, hw)

        // 2. 内側の色を決定
        // strokeWidthが極端に太い場合(例: 2px以上)のみ、厳密な補間計算を行う
        val (inCol0, inCol1, inCol2) = if (strokeWidth > 2.0f) {
            Triple(
                lerpColorInTriangle(p0.ix, p0.iy, x0, y0, x1, y1, x2, y2, col0, col1, col2),
                lerpColorInTriangle(p1.ix, p1.iy, x0, y0, x1, y1, x2, y2, col0, col1, col2),
                lerpColorInTriangle(p2.ix, p2.iy, x0, y0, x1, y1, x2, y2, col0, col1, col2),
            )
        } else {
            // 幅が狭い場合は、元の頂点色をそのまま使う（高速）
            Triple(col0, col1, col2)
        }

        // 3. 描画
        drawColoredEdge(p0, p1, inCol0, inCol1, col0, col1)
        drawColoredEdge(p1, p2, inCol1, inCol2, col1, col2)
        drawColoredEdge(p2, p0, inCol2, inCol0, col2, col0)
    }

    private fun drawColoredEdge(
        start: PointPair,
        end: PointPair,
        sInCol: Int,
        eInCol: Int,
        sOutCol: Int,
        eOutCol: Int,
    ) {
        // 頂点の指定順序: 開始外 -> 終了外 -> 終了内 -> 開始内
        // 色の指定順序もこれに完全に一致させる
        guiGraphics.fillQuad(
            start.ox, start.oy, // 1: 外側・開始
            end.ox, end.oy, // 2: 外側・終了
            end.ix, end.iy, // 3: 内側・終了
            start.ix, start.iy, // 4: 内側・開始
            sOutCol, // 1に対応
            eOutCol, // 2に対応
            eInCol, // 3に対応
            sInCol, // 4に対応
        )
    }

    private fun lerpColorInTriangle(
        px: Float,
        py: Float,
        x0: Float,
        y0: Float,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        c0: Int,
        c1: Int,
        c2: Int,
    ): Int {
        val denom = (y1 - y2) * (x0 - x2) + (x2 - x1) * (y0 - y2)
        // 縮退した三角形（面積0）の場合は安全にc0を返す
        if (abs(denom) < 1e-6f) return c0

        val w0 = ((y1 - y2) * (px - x2) + (x2 - x1) * (py - y2)) / denom
        val w1 = ((y2 - y0) * (px - x2) + (x0 - x2) * (py - y2)) / denom
        val w2 = 1f - w0 - w1

        // 0.0〜1.0にクランプ（浮動小数点の誤差対策）
        val cw0 = w0.coerceIn(0f, 1f)
        val cw1 = w1.coerceIn(0f, 1f)
        val cw2 = w2.coerceIn(0f, 1f)

        // アルファ値を安全に抽出 (0xFFL と Long を使うことで符号付きIntのバグを回避)
        val a0 = (c0 shr 24 and 0xFF).toFloat()
        val r0 = (c0 shr 16 and 0xFF).toFloat()
        val g0 = (c0 shr 8 and 0xFF).toFloat()
        val b0 = (c0 and 0xFF).toFloat()

        val a1 = (c1 shr 24 and 0xFF).toFloat()
        val r1 = (c1 shr 16 and 0xFF).toFloat()
        val g1 = (c1 shr 8 and 0xFF).toFloat()
        val b1 = (c1 and 0xFF).toFloat()

        val a2 = (c2 shr 24 and 0xFF).toFloat()
        val r2 = (c2 shr 16 and 0xFF).toFloat()
        val g2 = (c2 shr 8 and 0xFF).toFloat()
        val b2 = (c2 and 0xFF).toFloat()

        val a = (a0 * cw0 + a1 * cw1 + a2 * cw2).toInt()
        val r = (r0 * cw0 + r1 * cw1 + r2 * cw2).toInt()
        val g = (g0 * cw0 + g1 * cw1 + g2 * cw2).toInt()
        val b = (b0 * cw0 + b1 * cw1 + b2 * cw2).toInt()

        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }
}
