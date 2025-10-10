package melonystudios.mellowui.mixin.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.util.Alignment;
import melonystudios.mellowui.util.MellowUtils;
import melonystudios.mellowui.util.text.ScrollingText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(value = Widget.class, priority = 900)
public abstract class MUIWidgetMixin extends AbstractGui implements ScrollingText {
    @Unique private final RenderComponents components = RenderComponents.INSTANCE;
    @Shadow(remap = false) @Final public static int UNSET_FG_COLOR;
    @Shadow(remap = false) protected int packedFGColor;
    @Shadow public int x;
    @Shadow public int y;
    @Shadow protected int width;
    @Shadow protected int height;
    @Shadow public boolean active;
    @Shadow protected float alpha;
    @Shadow public boolean visible;
    @Shadow private boolean focused;
    @Shadow protected boolean isHovered;
    @Shadow private boolean wasHovered;
    @Shadow protected long nextNarration;
    @Shadow protected abstract void narrate();
    @Shadow public abstract boolean isHovered();
    @Shadow(remap = false) public abstract int getFGColor();
    @Shadow public abstract ITextComponent getMessage();
    @Shadow public abstract void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks);
    @Shadow public abstract void queueNarration(int delay);

    @Inject(method = "isMouseOver", at = @At("HEAD"), cancellable = true)
    public void isMouseOver(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> callback) {
        callback.setReturnValue(this.visible && mouseX >= (double) this.x && mouseY >= (double) this.y && mouseX < (double) (this.x + this.width) && mouseY < (double) (this.y + this.height));
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        callback.cancel();
        if (this.visible) {
            this.isHovered = this.components.containsPointInScissor(mouseX, mouseY) && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            if (this.wasHovered != this.isHovered()) {
                if (this.isHovered()) {
                    if (this.focused) this.queueNarration(200);
                    else this.queueNarration(750);
                } else {
                    this.nextNarration = Long.MAX_VALUE;
                }
            }

            if (this.visible) this.renderButton(stack, mouseX, mouseY, partialTicks);
            this.narrate();
            this.wasHovered = this.isHovered();
        }
    }

    @Inject(method = "renderButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/Widget;drawCenteredString(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/util/text/ITextComponent;III)V"), cancellable = true)
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (!MellowConfigs.CLIENT_CONFIGS.scrollingText.get()) return;
        callback.cancel();
        FontRenderer font = Minecraft.getInstance().font;
        int color = this.getFGColor();
        int padding = MathHelper.clamp(WidgetConfigs.WIDGET_CONFIGS.buttonTextPadding.get(), 0, this.width / 2 - 1);
        this.renderWidgetText(
                () -> this.renderScrollingString(stack, font, padding, color | MathHelper.ceil(this.alpha * 255F) << 24),
                () -> drawCenteredString(stack, font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, color | MathHelper.ceil(this.alpha * 255F) << 24)
        );
    }

    @Unique
    public void renderScrollingString(MatrixStack stack, FontRenderer font, int width, int color) {
        int minX = this.x + width;
        int maxX = this.x + this.width - width;
        this.renderAlignedScrollingText(stack, font, this.getMessage(), Alignment.CENTER, minX, this.y, maxX, this.y + this.height, color);
    }

    @Inject(method = "getFGColor", at = @At("HEAD"), cancellable = true, remap = false)
    public void getButtonTextColor(CallbackInfoReturnable<Integer> callback) {
        callback.cancel();
        if (this.packedFGColor != UNSET_FG_COLOR) callback.setReturnValue(this.packedFGColor);
        callback.setReturnValue(MellowUtils.getSelectableTextColor(this.isHovered(), this.active));
    }
}
