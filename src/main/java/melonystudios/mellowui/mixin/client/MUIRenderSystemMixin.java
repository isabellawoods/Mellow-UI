package melonystudios.mellowui.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.resource.theme.Themes;
import melonystudios.mellowui.util.debug.MUIDebuggingFlags;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RenderSystem.class)
public class MUIRenderSystemMixin {
    @ModifyVariable(method = "setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", at = @At("HEAD"), argsOnly = true)
    private static ResourceLocation setThemeTexture(ResourceLocation location) {
        if (Themes.theme().textures().containsKey(location) && (MUIDebuggingFlags.DEBUG_OOPS_ALL_TEXTURES || location.getPath().contains("textures/gui/"))) {
            return MellowUI.toTexturePath(Themes.theme().textures().get(location));
        }
        return location;
    }
}
