package org.infinite.mixin.graphics;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiGraphics.class)
public interface GuiGraphicsAccessor {

  @Accessor("hoveredTextStyle")
  void setHoveredTextStyle(Style style);

  @Accessor("clickableTextStyle")
  void setClickableTextStyle(Style style);

  @Accessor("guiRenderState")
  GuiRenderState getGuiRenderState();
}
