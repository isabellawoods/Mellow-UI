package melonystudios.mellowui.mixin.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.RenderComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Screen.class)
public abstract class MUIScreenMixin extends AbstractContainerEventHandler {
    @Unique
    private final RenderComponents components = RenderComponents.INSTANCE;
    @Shadow
    @Nullable
    protected Minecraft minecraft;
    @Shadow
    public int height;
    @Shadow
    public int width;

    @Inject(method = "renderBackground(Lcom/mojang/blaze3d/vertex/PoseStack;I)V", at = @At("HEAD"), cancellable = true)
    public void renderBackground(PoseStack stack, int vOffset, CallbackInfo callback) {
        if (!MellowConfigs.CLIENT_CONFIGS.updateScreenBackground.get()) return;
        callback.cancel();
        float partialTicks = this.minecraft.getDeltaFrameTime();
        this.components.renderBackground(this.minecraft.screen, partialTicks, vOffset, this.width, this.height);
    }

    @Inject(method = "renderDirtBackground", at = @At("HEAD"), cancellable = true)
    public void renderTransparentBackground(int vOffset, CallbackInfo callback) {
        if (!MellowConfigs.CLIENT_CONFIGS.updateScreenBackground.get()) return;
        callback.cancel();
        this.components.renderMenuBackground(0, 0, this.width, this.height, vOffset);
        MinecraftForge.EVENT_BUS.post(new ScreenEvent.BackgroundDrawnEvent(this.minecraft.screen, this.components.poseStack()));
    }
}
