package melonystudios.mellowui.screen.panel;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.screen.RenderComponents;

/// Defines a single entry inside a {@link Panel}.
public abstract class PanelEntry {
    protected final Panel panel;

    /// Defines a single entry inside a {@link Panel}.
    /// @param panel The parent panel.
    public PanelEntry(Panel panel) {
        this.panel = panel;
    }

    /// Renders this panel entry.
    /// @param stack The default {@link PoseStack} used for rendering.
    /// @param components *Mellow UI*'s {@linkplain RenderComponents render components}, used for rendering or getting various things.
    /// @param x The x position of this entry.
    /// @param y The y position of this entry.
    /// @param width The width of this entry.
    /// @param height The height of this entry.
    public void renderEntry(PoseStack stack, RenderComponents components, int x, int y, int width, int height) {}

    /// Gets the height, in **Minecraft Pixels** (`mpx`), of the content being rendered.
    public abstract int getContentHeight();
}
