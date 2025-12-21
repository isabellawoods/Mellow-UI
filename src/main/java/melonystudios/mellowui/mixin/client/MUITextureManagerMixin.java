package melonystudios.mellowui.mixin.client;

import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.resource.theme.Themes;
import melonystudios.mellowui.util.DebuggingFlags;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TextureManager.class)
public class MUITextureManagerMixin {
    @ModifyVariable(method = "bind", at = @At("HEAD"), argsOnly = true)
    public ResourceLocation bindThemeTexture(ResourceLocation location) {
        if (Themes.theme().textures().containsKey(location) && (DebuggingFlags.DEBUG_OOPS_ALL_TEXTURES || location.getPath().contains("textures/gui/"))) {
            return MellowUI.toTexturePath(Themes.theme().textures().get(location));
        }
        return location;
    }
}
