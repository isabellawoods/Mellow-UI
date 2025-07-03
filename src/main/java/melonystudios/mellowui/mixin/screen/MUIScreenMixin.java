package melonystudios.mellowui.mixin.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Screen.class)
public abstract class MUIScreenMixin extends FocusableGui {
    @Shadow
    @Nullable
    protected Minecraft minecraft;
    @Shadow
    public int height;
    @Shadow
    public int width;
    @Shadow
    public abstract void renderDirtBackground(int vOffset);

    // eventually I'll do something about this to make it compatible with other mods (especially jei) ~isa 2-7-25
    @Inject(method = "renderBackground(Lcom/mojang/blaze3d/matrix/MatrixStack;I)V", at = @At("HEAD"), cancellable = true)
    public void renderBackground(MatrixStack stack, int vOffset, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.updateScreenBackground.get()) {
            callback.cancel();
            float partialTicks = this.minecraft.getDeltaFrameTime();
            if (this.minecraft.level != null) {
                if (MellowConfigs.CLIENT_CONFIGS.blurryContainers.get()) {
                    MellowUtils.renderBlurredBackground(partialTicks);
                } else if (!(this.minecraft.screen instanceof ContainerScreen)) {
                    MellowUtils.renderBlurredBackground(partialTicks);
                }

                RenderSystem.enableBlend();
                // #C0101010 to #D0101010 (Alpha: 192 to 208)
                if (MellowConfigs.CLIENT_CONFIGS.gradientBackground.get() || this.minecraft.screen instanceof ContainerScreen) {
                    this.minecraft.getTextureManager().bind(GUITextures.INWORLD_GRADIENT);
                    blit(stack, 0, 0, this.width, this.height, 0, 0, 16, 128, 16, 128);
                } else {
                    MellowUtils.renderBackground(stack, this.width, this.height, vOffset);
                }
                RenderSystem.disableBlend();
                MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.BackgroundDrawnEvent(this.minecraft.screen, stack));
            } else {
                MellowUtils.renderPanorama(stack, partialTicks, this.width, this.height, 1);
                MellowUtils.renderBlurredBackground(partialTicks);
                this.renderDirtBackground(vOffset);
            }
        }
    }

    @Inject(method = "renderDirtBackground", at = @At("HEAD"), cancellable = true)
    public void renderTransparentBackground(int vOffset, CallbackInfo callback) {
        MatrixStack stack = new MatrixStack();
        if (MellowConfigs.CLIENT_CONFIGS.updateScreenBackground.get()) {
            callback.cancel();
            MellowUtils.renderBackground(stack, this.width, this.height, vOffset);
            MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.BackgroundDrawnEvent(this.minecraft.screen, stack));
        }
    }
}
