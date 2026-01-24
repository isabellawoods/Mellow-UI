package melonystudios.mellowui.mixin.client;

import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.resource.theme.Themes;
import melonystudios.mellowui.util.debug.MUIDebuggingFlags;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TextureManager.class)
public class MUITextureManagerMixin {
    @ModifyVariable(method = "bindForSetup", at = @At("HEAD"), argsOnly = true)
    public ResourceLocation bindThemeTextureForSetup(ResourceLocation location) {
        if (Themes.theme().textures().containsKey(location) && (MUIDebuggingFlags.DEBUG_OOPS_ALL_TEXTURES || location.getPath().contains("textures/gui/"))) {
            return MellowUI.toTexturePath(Themes.theme().textures().get(location));
        }
        return location;
    }
}
