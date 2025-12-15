package melonystudios.mellowui.mixin.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.element.RenderComponents;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.NotificationModUpdateScreen;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.loading.FMLConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NotificationModUpdateScreen.class, remap = false)
public class MUINotificationModUpdateScreenMixin extends Screen {
    @Shadow
    private VersionChecker.Status showNotification;
    @Shadow
    @Final
    private Button modButton;

    public MUINotificationModUpdateScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true, remap = true)
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (!MellowConfigs.CLIENT_CONFIGS.updateAvailableIconStyle.get()) return;
        callback.cancel();
        if (this.showNotification == null || !this.showNotification.shouldDraw() || !FMLConfig.runVersionCheck()) return;
        RenderComponents.INSTANCE.renderUpdateAvailableIcon(this.modButton.x, this.modButton.y, this.modButton.getWidth(), this.modButton.getHeight(), 1, false, this.showNotification);
    }
}
