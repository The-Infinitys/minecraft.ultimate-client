package org.infinite.ultimate

import org.infinite.features.local.rendering.LocalRenderingCategory
import org.infinite.libs.core.features.categories.LocalFeatureCategories

class UltimateLocalFeatures : LocalFeatureCategories() {
    val rendering by category(LocalRenderingCategory())
}
