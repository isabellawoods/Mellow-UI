package melonystudios.mellowui.mixin.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.gui.screen.CreateBuffetWorldScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateBuffetWorldScreen.class)
public class MUIBuffetWorldScreenMixin extends Screen {
    public MUIBuffetWorldScreenMixin(ITextComponent title) {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        MellowUtils.renderPanorama(stack, partialTicks, this.width, this.height, 1);
        MellowUtils.renderBlurredBackground(partialTicks);
    }
}
