package org.infinite.libs.graphics.graphics2d.text;

import net.minecraft.client.gui.font.FontSet;
import net.minecraft.network.chat.Style;

public interface IModernFontManager {
  @SuppressWarnings("unused")
  FontSet ultimate$fontSetFromStyle(Style style);

  @SuppressWarnings("unused")
  FontSet ultimate$fontSetFromIdentifier(String name);
}
