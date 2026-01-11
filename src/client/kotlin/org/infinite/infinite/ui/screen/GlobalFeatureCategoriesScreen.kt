package org.infinite.infinite.ui.screen

import net.minecraft.client.gui.screens.Screen
import org.infinite.InfiniteClient
import org.infinite.infinite.ui.widget.GlobalCategoryWidget
import org.infinite.libs.core.features.categories.category.GlobalCategory
import org.infinite.libs.core.features.feature.GlobalFeature
import kotlin.reflect.KClass

class GlobalFeatureCategoriesScreen(parent: Screen? = null) :
    FeatureCategoriesScreen<
        KClass<out GlobalFeature>,
        GlobalFeature,
        GlobalCategory,
        GlobalCategoryWidget, // Global専用のWidget
        >(parent) {

    // Globalのカテゴリデータソースを取得
    override val dataSource: List<GlobalCategory>
        get() = InfiniteClient.globalFeatures.categories.values.toList()

    // Global用のWidgetをインスタンス化
    override fun createWidget(
        index: Int,
        data: GlobalCategory,
    ): GlobalCategoryWidget {
        return GlobalCategoryWidget(0, 0, 120, 180, data, this, index)
    }
}
