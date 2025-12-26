package org.infinite.libs.graphics.graphics2d.text;

import net.minecraft.client.gui.font.FontSet;
import net.minecraft.network.chat.Style;

public interface IModernFontManager {
  FontSet ultimate$fontSetFromStyle(Style style);

  FontSet ultimate$fontSetFromIdentifier(String name);
}
