package org.infinite.infinite.features.local.combat.attack

import org.infinite.libs.core.features.feature.LocalFeature
import org.infinite.libs.core.features.property.selection.EnumSelectionProperty

class CriticalFeature : LocalFeature() {

    enum class CriticalMode {
        Packet, // パケット偽装
        MiniJump, // 微小な速度追加
        FullJump, // 通常ジャンプ
    }

    // 以前のコードで定義したプロパティシステムを使用
    val mode by property(EnumSelectionProperty(CriticalMode.Packet))
}
