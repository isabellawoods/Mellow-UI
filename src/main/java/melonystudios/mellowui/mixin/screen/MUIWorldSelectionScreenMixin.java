package melonystudios.mellowui.mixin.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldSelectionScreen.class)
public class MUIWorldSelectionScreenMixin extends Screen {
    public MUIWorldSelectionScreenMixin(ITextComponent title) {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        this.renderBackground(stack);
    }
}
