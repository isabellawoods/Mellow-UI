package melonystudios.mellowui.mixin.forge;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.element.RenderComponents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
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

    public MUINotificationModUpdateScreenMixin(ITextComponent title) {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true, remap = true)
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (!MellowConfigs.CLIENT_CONFIGS.updateAvailableIconStyle.get()) return;
        callback.cancel();
        if (this.showNotification == null || !this.showNotification.shouldDraw() || !FMLConfig.runVersionCheck()) return;
        RenderComponents.INSTANCE.renderUpdateAvailableIcon(this.modButton.x, this.modButton.y, this.modButton.getWidth(), this.modButton.getHeight(), 1, false, this.showNotification);
    }
}
