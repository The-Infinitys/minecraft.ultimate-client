package org.infinite.infinite.features.local.movement

import org.infinite.infinite.features.local.movement.fly.SuperFly
import org.infinite.libs.core.features.categories.category.LocalCategory

class LocalMovementCategory : LocalCategory() {
    val superFly by feature(SuperFly())
}
