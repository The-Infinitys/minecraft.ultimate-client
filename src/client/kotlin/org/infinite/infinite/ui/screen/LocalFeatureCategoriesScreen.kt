package org.infinite.infinite.ui.screen

import org.infinite.InfiniteClient
import org.infinite.infinite.ui.widget.LocalCategoryWidget
import org.infinite.libs.core.features.categories.category.LocalCategory
import org.infinite.libs.core.features.feature.LocalFeature
import kotlin.reflect.KClass

class LocalFeatureCategoriesScreen :
    FeatureCategoriesScreen<
        KClass<out LocalFeature>,
        LocalFeature,
        LocalCategory,
        LocalCategoryWidget, // ここで使用する具体的なWidgetクラスを指定
        >() {

    override val dataSource: List<LocalCategory>
        get() = InfiniteClient.localFeatures.categories.values.toList()

    override fun createWidget(
        index: Int,
        data: LocalCategory,
    ): LocalCategoryWidget {
        return LocalCategoryWidget(0, 0, 120, 180, data, this, index)
    }
}
