package org.infinite.libs.graphics.graphics2d.screen;

import java.util.List;
import net.minecraft.client.gui.components.Renderable;

public interface IScreen {
  void infinite$setRenderable(List<Renderable> value);

  List<Renderable> infinite$getRenderable();
}
