package org.infinite.mixin.graphics;

import java.util.Map;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import org.infinite.libs.graphics.graphics2d.text.IModernFontManager;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(FontManager.class)
public abstract class FontManagerMixin implements IModernFontManager {
  @Shadow @Final private Map<Identifier, FontSet> fontSets;

  @Unique
  private static final Identifier REGULAR =
      Identifier.fromNamespaceAndPath("minecraft", "infinite_regular");

  @Unique
  private static final Identifier BOLD =
      Identifier.fromNamespaceAndPath("minecraft", "infinite_bold");

  @Unique
  private static final Identifier ITALIC =
      Identifier.fromNamespaceAndPath("minecraft", "infinite_italic");

  @Unique
  private static final Identifier BOLD_ITALIC =
      Identifier.fromNamespaceAndPath("minecraft", "infinite_bold_italic");

  @Override
  public @NonNull FontSet ultimate$fontSetFromStyle(Style style) {
    Identifier targetId;
    if (style.isBold() && style.isItalic()) {
      targetId = BOLD_ITALIC;
    } else if (style.isBold()) {
      targetId = BOLD;
    } else if (style.isItalic()) {
      targetId = ITALIC;
    } else {
      targetId = REGULAR;
    }

    FontSet set = this.fontSets.get(targetId);
    if (set == null) {
      // 見つからない場合はデフォルトのフォントセットを返す
      return this.fontSets.get(Identifier.fromNamespaceAndPath("minecraft", "default"));
    }
    return set;
  }

  @Override
  public @NonNull FontSet ultimate$fontSetFromIdentifier(@NonNull String name) {
    Identifier targetId = Identifier.fromNamespaceAndPath("minecraft", name);
    FontSet set = this.fontSets.get(targetId);
    if (set == null) {
      return this.fontSets.get(Identifier.fromNamespaceAndPath("minecraft", "uniform"));
    }
    return set;
  }
}
