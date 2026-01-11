package org.infinite.infinite.theme.infinite

import org.infinite.libs.ui.theme.ColorScheme

class InfiniteColorScheme : ColorScheme() {
    override val backgroundColor: Int
        get() = blackColor
    override val foregroundColor: Int
        get() = whiteColor
    override val accentColor: Int
        get() {
            val interval = 10000.0
            val time = System.currentTimeMillis()
            val t = time % interval / interval
            return color((360f * t).toFloat(), 1f, 0.5f, 1f)
        }
}
