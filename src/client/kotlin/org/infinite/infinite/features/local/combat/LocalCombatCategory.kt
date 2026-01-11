package org.infinite.infinite.features.local.combat

import org.infinite.infinite.features.local.combat.attack.CriticalFeature
import org.infinite.libs.core.features.categories.category.LocalCategory

@Suppress("Unused")
class LocalCombatCategory : LocalCategory() {
    val critical by feature(CriticalFeature())
}
