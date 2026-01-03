package org.infinite.libs.graphics.graphics2d.structs

import net.minecraft.client.Minecraft

class TextStyle(
    var shadow: Boolean = false,
    var size: Float = Minecraft.getInstance().font.lineHeight.toFloat(),
    var font: String = "default",
)
