package melonystudios.mellowui.resource.gui;

import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class GUITextureManager extends TextureAtlasHolder {
    private final Set<ResourceLocation> registeredSprites = new HashSet<>();

    public GUITextureManager(TextureManager manager) {
        super(manager, GUITextures.GUI_SPRITES_ATLAS, "gui");
    }

    public void registerSprite(ResourceLocation location) {
        this.registeredSprites.add(location);
    }

    @Override
    @Nonnull
    protected Stream<ResourceLocation> getResourcesToLoad() {
        return Collections.unmodifiableSet(this.registeredSprites).stream();
    }

    @Override
    @Nonnull
    public TextureAtlasSprite getSprite(ResourceLocation location) {
        return super.getSprite(location);
    }
}
