package org.infinite.features.global.rendering

import org.infinite.features.global.rendering.font.UltimateFontFeature
import org.infinite.libs.core.features.categories.category.GlobalCategory

class GlobalRenderingCategory : GlobalCategory() {
    val ultimateFontFeature by feature(UltimateFontFeature())
}
