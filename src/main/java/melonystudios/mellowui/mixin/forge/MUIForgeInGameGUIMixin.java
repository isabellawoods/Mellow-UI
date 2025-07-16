package melonystudios.mellowui.mixin.forge;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ForgeIngameGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ForgeIngameGui.class, remap = false)
public abstract class MUIForgeInGameGUIMixin extends Gui {
    @Shadow
    private Font font;

    public MUIForgeInGameGUIMixin(Minecraft minecraft) {
        super(minecraft);
    }

    @Inject(method = "renderRecordOverlay", at = @At("HEAD"), cancellable = true)
    protected void renderRecordOverlay(int width, int height, float partialTicks, PoseStack stack, CallbackInfo callback) {
        callback.cancel();
        if (this.overlayMessageTime > 0 && this.overlayMessageString != null) {
            this.minecraft.getProfiler().push("overlayMessage");
            float hue = (float) this.overlayMessageTime - partialTicks;
            int opacity = (int) (hue * 255F / 20F);
            if (opacity > 255) opacity = 255;

            if (opacity > 8) {
                stack.pushPose();
                stack.translate((float) (width / 2), (float) (height - 68), 0F);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                int color = (this.animateOverlayMessageColor ? Mth.hsvToRgb(hue / 50F, 0.7F, 0.6F) & 0xFFFFFF : 0xFFFFFF);
                this.drawBackdrop(stack, this.font, -4, this.font.width(this.overlayMessageString), 16777215 | (opacity << 24));
                this.font.drawShadow(stack, this.overlayMessageString.getVisualOrderText(), -this.font.width(this.overlayMessageString) / 2, -4, color | (opacity << 24));
                RenderSystem.disableBlend();
                stack.popPose();
            }

            this.minecraft.getProfiler().pop();
        }
    }
}
