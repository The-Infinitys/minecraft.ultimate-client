package org.infinite.mixin.graphics;

import java.util.List;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import org.infinite.libs.graphics.graphics2d.screen.IScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Screen.class)
public abstract class ScreenMixin implements IScreen {
  @Shadow @Final private List<Renderable> renderables;

  @Override
  public void infinite$setRenderable(List<Renderable> value) {
    renderables.clear();
    renderables.addAll(value);
  }

  @Override
  public List<Renderable> infinite$getRenderable() {
    return renderables;
  }
}
