package melonystudios.mellowui.mixin.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ImageButton.class)
public class MUIImageButtonMixin extends Button {
    public MUIImageButtonMixin(int x, int y, int width, int height, Component text, OnPress onPress) {
        super(x, y, width, height, text, onPress);
    }

    @Inject(method = "renderButton", at = @At("HEAD"))
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        RenderSystem.setShaderColor(1, 1, 1, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }
}
