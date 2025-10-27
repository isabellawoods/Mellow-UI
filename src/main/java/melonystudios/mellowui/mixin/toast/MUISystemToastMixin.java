package melonystudios.mellowui.mixin.toast;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.config.WidgetConfigs;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mixin(SystemToast.class)
public abstract class MUISystemToastMixin implements IToast {
    @Shadow protected abstract void renderBackgroundRow(MatrixStack stack, ToastGui toast, int width, int textureV, int y, int height);
    @Shadow private List<IReorderingProcessor> messageLines;
    @Shadow private ITextComponent title;
    @Shadow private long lastChanged;
    @Shadow private boolean changed;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack stack, ToastGui toast, long timeSinceLastChanged, CallbackInfoReturnable<IToast.Visibility> callback) {
        callback.cancel();
        if (this.changed) {
            this.lastChanged = timeSinceLastChanged;
            this.changed = false;
        }

        toast.getMinecraft().getTextureManager().bind(TEXTURE);
        RenderSystem.color3f(1, 1, 1);
        int width = this.width();
        int height = this.height();

        if (width == 160 && this.messageLines.size() <= 1) {
            toast.blit(stack, 0, 0, 0, 64, width, height);
        } else {
            int y = this.height() + Math.max(0, this.messageLines.size() - 1) * 12;
            int vOffset = Math.min(4, y - 28);
            this.renderBackgroundRow(stack, toast, width, 0, 0, 28);

            for (int i = 28; i < y - vOffset; i += 10) {
                this.renderBackgroundRow(stack, toast, width, 16, i, Math.min(16, y - i - vOffset));
            }

            this.renderBackgroundRow(stack, toast, width, 32 - vOffset, y - vOffset, vOffset);
        }

        if (this.messageLines == null || this.messageLines.isEmpty()) {
            AbstractGui.drawString(stack, toast.getMinecraft().font, this.title, 18, (height - 8) / 2, WidgetConfigs.WIDGET_CONFIGS.systemToastTitleColor.get());
        } else {
            AbstractGui.drawString(stack, toast.getMinecraft().font, this.title, 18, 7, WidgetConfigs.WIDGET_CONFIGS.systemToastTitleColor.get());

            for (int line = 0; line < this.messageLines.size(); ++line) {
                toast.getMinecraft().font.drawShadow(stack, this.messageLines.get(line), 18, (float) (18 + line * 12), WidgetConfigs.WIDGET_CONFIGS.systemToastDescriptionColor.get());
            }
        }

        callback.setReturnValue(timeSinceLastChanged - this.lastChanged < 5000L ? IToast.Visibility.SHOW : IToast.Visibility.HIDE);
    }
}
