package melonystudios.mellowui.mixin.forge;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.gui.ForgeIngameGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ForgeIngameGui.class, remap = false)
public abstract class MUIForgeInGameGUIMixin extends IngameGui {
    @Shadow
    private FontRenderer fontrenderer;

    public MUIForgeInGameGUIMixin(Minecraft minecraft) {
        super(minecraft);
    }

    @Inject(method = "renderRecordOverlay", at = @At("HEAD"), cancellable = true)
    protected void renderRecordOverlay(int width, int height, float partialTicks, MatrixStack stack, CallbackInfo callback) {
        callback.cancel();
        if (this.overlayMessageTime > 0 && this.overlayMessageString != null) {
            this.minecraft.getProfiler().push("overlayMessage");
            float hue = (float) this.overlayMessageTime - partialTicks;
            int opacity = (int) (hue * 255F / 20F);
            if (opacity > 255) opacity = 255;

            if (opacity > 8) {
                RenderSystem.pushMatrix();
                RenderSystem.translatef((float) (width / 2), (float) (height - 68), 0F);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                int color = (this.animateOverlayMessageColor ? MathHelper.hsvToRgb(hue / 50F, 0.7F, 0.6F) & 0xFFFFFF : 0xFFFFFF);
                this.drawBackdrop(stack, this.fontrenderer, -4, this.fontrenderer.width(this.overlayMessageString), 16777215 | (opacity << 24));
                this.fontrenderer.drawShadow(stack, this.overlayMessageString.getVisualOrderText(), -this.fontrenderer.width(this.overlayMessageString) / 2, -4, color | (opacity << 24));
                RenderSystem.disableBlend();
                RenderSystem.popMatrix();
            }

            this.minecraft.getProfiler().pop();
        }
    }
}
