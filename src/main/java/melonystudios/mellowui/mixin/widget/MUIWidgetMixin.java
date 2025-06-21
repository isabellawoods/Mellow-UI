package melonystudios.mellowui.mixin.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.screen.widget.ScrollingText;
import melonystudios.mellowui.util.MellowUtils;
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
    @Shadow(remap = false) @Final public static int UNSET_FG_COLOR;
    @Shadow(remap = false) protected int packedFGColor;
    @Shadow public int x;
    @Shadow public int y;
    @Shadow protected int width;
    @Shadow protected int height;
    @Shadow public boolean active;
    @Shadow protected float alpha;
    @Shadow public abstract boolean isHovered();
    @Shadow(remap = false) public abstract int getFGColor();
    @Shadow public abstract ITextComponent getMessage();
    @Shadow protected abstract int getYImage(boolean hovered);
    @Shadow protected abstract void renderBg(MatrixStack stack, Minecraft minecraft, int mouseX, int mouseY);

    @Inject(method = "renderButton", at = @At("HEAD"), cancellable = true)
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.scrollingText.get()) {
            callback.cancel();
            Minecraft minecraft = Minecraft.getInstance();
            FontRenderer font = minecraft.font;
            minecraft.getTextureManager().bind(Widget.WIDGETS_LOCATION);
            RenderSystem.color4f(1, 1, 1, this.alpha);
            int yImage = this.getYImage(this.isHovered());
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            this.blit(stack, this.x, this.y, 0, 46 + yImage * 20, this.width / 2, this.height);
            this.blit(stack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + yImage * 20, this.width / 2, this.height);
            this.renderBg(stack, minecraft, mouseX, mouseY);
            int color = this.getFGColor();
            int padding = MathHelper.clamp(WidgetConfigs.WIDGET_CONFIGS.buttonTextBorderPadding.get(), 0, this.width / 2 - 1);
            this.renderScrollingString(stack, font, padding, color | MathHelper.ceil(this.alpha * 255F) << 24);
        }
    }

    @Unique
    public void renderScrollingString(MatrixStack stack, FontRenderer font, int width, int color) {
        int minX = this.x + width;
        int maxX = this.x + this.width - width;
        this.renderScrollingString(stack, font, this.getMessage(), minX, this.y, maxX, this.y + this.height, color);
    }

    @Inject(method = "getFGColor", at = @At("HEAD"), cancellable = true, remap = false)
    public void getFGColor(CallbackInfoReturnable<Integer> callback) {
        callback.cancel();
        if (this.packedFGColor != UNSET_FG_COLOR) callback.setReturnValue(this.packedFGColor);
        callback.setReturnValue(MellowUtils.getSelectableTextColor(this.isHovered(), this.active));
    }
}
