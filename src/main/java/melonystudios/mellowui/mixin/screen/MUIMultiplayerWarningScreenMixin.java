package melonystudios.mellowui.mixin.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.screen.RenderComponents;
import net.minecraft.client.gui.screen.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerWarningScreen.class)
public class MUIMultiplayerWarningScreenMixin extends Screen {
    @Unique
    private final RenderComponents components = RenderComponents.INSTANCE;

    public MUIMultiplayerWarningScreenMixin(ITextComponent title) {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        this.components.renderPanorama(partialTicks, this.width, this.height, 1);
        this.components.renderBlurredBackground(partialTicks);
    }
}
