package org.infinite.mixin.graphics;

import java.util.function.Consumer;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Style;
import org.infinite.libs.graphics.text.ModernTextRenderer;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
  @Inject(
      method =
          "textRenderer(Lnet/minecraft/client/gui/GuiGraphics$HoveredTextEffects;Ljava/util/function/Consumer;)Lnet/minecraft/client/gui/ActiveTextCollector;",
      at = @At("HEAD"),
      cancellable = true)
  public void modernTextRenderer(
      GuiGraphics.HoveredTextEffects hoveredTextEffects,
      @Nullable Consumer<Style> consumer,
      CallbackInfoReturnable<ActiveTextCollector> cir) {
    cir.setReturnValue(
        new ModernTextRenderer((GuiGraphics) (Object) this, hoveredTextEffects, consumer));
  }
}
