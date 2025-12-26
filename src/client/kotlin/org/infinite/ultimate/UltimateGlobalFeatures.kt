package org.infinite.ultimate

import org.infinite.features.global.rendering.GlobalRenderingCategory
import org.infinite.libs.core.features.categories.GlobalFeatureCategories

class UltimateGlobalFeatures : GlobalFeatureCategories() {
    val rendering by category(GlobalRenderingCategory())
}
