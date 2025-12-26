package org.infinite.mixin.graphics;

import java.util.Map;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FontManager.class)
public abstract class FontManagerAccessor {
  @Accessor("fontSets")
  public abstract Map<Identifier, FontSet> getFontSets();
}
