package melonystudios.mellowui.mixin.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.ScrollingText;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
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
@Mixin(value = AbstractWidget.class, priority = 900)
public abstract class MUIAbstractWidgetMixin extends GuiComponent implements ScrollingText {
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
    @Shadow protected boolean isHovered;
    @Shadow public abstract boolean isHoveredOrFocused();
    @Shadow(remap = false) public abstract int getFGColor();
    @Shadow public abstract Component getMessage();
    @Shadow public abstract void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks);

    @Inject(method = "isMouseOver", at = @At("HEAD"), cancellable = true)
    public void isMouseOver(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> callback) {
        callback.setReturnValue(this.visible && mouseX >= (double) this.x && mouseY >= (double) this.y && mouseX < (double) (this.x + this.width) && mouseY < (double) (this.y + this.height));
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        callback.cancel();
        if (this.visible) {
            this.isHovered = this.components.containsPointInScissor(mouseX, mouseY) && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            this.renderButton(stack, mouseX, mouseY, partialTicks);
        }
    }

    @Inject(method = "renderButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractWidget;drawCenteredString(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"), cancellable = true)
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (!MellowConfigs.CLIENT_CONFIGS.scrollingText.get()) return;
        callback.cancel();
        Font font = Minecraft.getInstance().font;
        int color = this.getFGColor();
        int padding = Mth.clamp(WidgetConfigs.WIDGET_CONFIGS.buttonTextPadding.get(), 0, this.width / 2 - 1);
        this.renderWidgetText(
                () -> this.renderScrollingString(font, padding, color | Mth.ceil(this.alpha * 255F) << 24),
                () -> drawCenteredString(stack, font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, color | Mth.ceil(this.alpha * 255F) << 24)
        );
        if (this.isHovered && this.active) RenderComponents.INSTANCE.requestCursor(CursorTypes.POINTING_HAND);
    }

    @Unique
    public void renderScrollingString(Font font, int width, int color) {
        int minX = this.x + width;
        int maxX = this.x + this.width - width;
        this.renderAlignedScrollingText(font, this.getMessage(), Alignment.CENTER, minX, this.y, maxX, this.y + this.height, color);
    }

    @Inject(method = "getFGColor", at = @At("HEAD"), cancellable = true, remap = false)
    public void getButtonTextColor(CallbackInfoReturnable<Integer> callback) {
        callback.cancel();
        if (this.packedFGColor != UNSET_FG_COLOR) callback.setReturnValue(this.packedFGColor);
        callback.setReturnValue(TextComponents.selectableColor(this.isHoveredOrFocused(), this.active));
    }
}
