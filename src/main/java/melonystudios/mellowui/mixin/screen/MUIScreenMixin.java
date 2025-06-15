package melonystudios.mellowui.mixin.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.util.GUICompatUtils;
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

    @Inject(method = "renderBackground(Lcom/mojang/blaze3d/matrix/MatrixStack;I)V", at = @At("HEAD"), cancellable = true)
    public void renderBackground(MatrixStack stack, int vOffset, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.updateScreenBackground.get()) {
            callback.cancel();
            if (this.minecraft.level != null) {
                if (MellowConfigs.CLIENT_CONFIGS.blurryContainers.get()) {
                    MellowUtils.renderBlurredBackground(this.minecraft.getDeltaFrameTime());
                } else if (!(this.minecraft.screen instanceof ContainerScreen)) {
                    MellowUtils.renderBlurredBackground(this.minecraft.getDeltaFrameTime());
                }

                // #C0101010 to #D0101010 (Alpha: 192 to 208)
                RenderSystem.enableBlend();
                this.minecraft.getTextureManager().bind(GUITextures.INWORLD_GRADIENT);
                blit(stack, 0, 0, 0, 0, this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight(), this.minecraft.getWindow().getScreenWidth(), this.minecraft.getWindow().getScreenHeight());
                RenderSystem.disableBlend();
                MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.BackgroundDrawnEvent(this.minecraft.screen, stack));
            } else {
                MellowUtils.renderPanorama(stack, this.minecraft.getDeltaFrameTime(), this.width, this.height, 1);
                MellowUtils.renderBlurredBackground(this.minecraft.getDeltaFrameTime());
                this.renderDirtBackground(vOffset);
            }
        }
    }

    @Inject(method = "renderDirtBackground", at = @At("HEAD"), cancellable = true)
    public void renderTransparentBackground(int vOffset, CallbackInfo callback) {
        MatrixStack stack = new MatrixStack();
        if (MellowConfigs.CLIENT_CONFIGS.updateScreenBackground.get()) {
            callback.cancel();
            if (!GUICompatUtils.hasCustomBackground(this.minecraft, stack, this.width, this.height, vOffset)) {
                MellowUtils.renderBackground(stack, this.width, this.height, vOffset);
            }
            MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.BackgroundDrawnEvent(this.minecraft.screen, stack));
        }
    }
}
