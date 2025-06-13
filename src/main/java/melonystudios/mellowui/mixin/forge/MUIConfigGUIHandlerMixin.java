package melonystudios.mellowui.mixin.forge;

import melonystudios.mellowui.screen.option.ForgeOptionsScreen;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.ConfigGuiHandler;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.BiFunction;

@OnlyIn(Dist.CLIENT)
@Mixin(ConfigGuiHandler.class)
public class MUIConfigGUIHandlerMixin {
    @Inject(method = "getGuiFactoryFor", at = @At("HEAD"), cancellable = true)
    private static void getGuiFactoryFor(ModInfo selectedMod, CallbackInfoReturnable<Optional<BiFunction<Minecraft, Screen, Screen>>> callback) {
        if (selectedMod.getModId().equals("minecraft")) {
            callback.setReturnValue(Optional.of((minecraft, lastScreen) -> MellowUtils.options(lastScreen, minecraft)));
        } else if (selectedMod.getModId().equals("forge")) {
            callback.setReturnValue(Optional.of((minecraft, lastScreen) -> new ForgeOptionsScreen(lastScreen, minecraft.options)));
        }
    }
}
