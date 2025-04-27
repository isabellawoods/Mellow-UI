package melonystudios.mellowui.mixin.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ImageButton.class)
public class MUIImageButtonMixin extends Button {
    public MUIImageButtonMixin(int x, int y, int width, int height, ITextComponent text, IPressable whenPressed) {
        super(x, y, width, height, text, whenPressed);
    }

    @Inject(method = "renderButton", at = @At("HEAD"))
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        RenderSystem.color4f(1, 1, 1, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }
}
