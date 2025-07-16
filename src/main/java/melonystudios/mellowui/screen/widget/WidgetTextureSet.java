package melonystudios.mellowui.screen.widget;

import net.minecraft.resources.ResourceLocation;

public class WidgetTextureSet {
    private final ResourceLocation defaultTexture;
    private final ResourceLocation highlightedTexture;
    private final ResourceLocation disabledTexture;

    public WidgetTextureSet(ResourceLocation defaultTexture, ResourceLocation highlightedTexture, ResourceLocation disabledTexture) {
        this.defaultTexture = defaultTexture;
        this.highlightedTexture = highlightedTexture;
        this.disabledTexture = disabledTexture;
    }

    public WidgetTextureSet(ResourceLocation defaultTexture, ResourceLocation highlightedTexture) {
        this(defaultTexture, highlightedTexture, defaultTexture);
    }

    public ResourceLocation getWidgetTexture(boolean highlighted, boolean active) {
        if (!active) {
            return this.disabledTexture();
        } else if (highlighted) {
            return this.highlightedTexture();
        } else {
            return this.defaultTexture();
        }
    }

    public ResourceLocation defaultTexture() {
        return this.defaultTexture;
    }

    public ResourceLocation highlightedTexture() {
        return this.highlightedTexture;
    }

    public ResourceLocation disabledTexture() {
        return this.disabledTexture;
    }
}
