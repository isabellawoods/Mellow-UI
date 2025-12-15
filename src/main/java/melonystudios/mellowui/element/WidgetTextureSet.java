package melonystudios.mellowui.element;

import net.minecraft.resources.ResourceLocation;

public record WidgetTextureSet(ResourceLocation defaultTexture, ResourceLocation highlightedTexture, ResourceLocation disabledTexture) {
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
}
