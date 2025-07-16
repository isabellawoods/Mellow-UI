package melonystudios.mellowui.mixin.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.screen.widget.ScrollingText;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
    @Shadow @Final public static ResourceLocation WIDGETS_LOCATION;
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
    @Shadow protected abstract int getYImage(boolean hovered);
    @Shadow public abstract void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks);
    @Shadow protected abstract void renderBg(PoseStack stack, Minecraft minecraft, int mouseX, int mouseY);

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        callback.cancel();
        if (this.visible) {
            this.isHovered = this.components.containsPointInScissor(mouseX, mouseY) && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            this.renderButton(stack, mouseX, mouseY, partialTicks);
        }
    }

    @Inject(method = "renderButton", at = @At("HEAD"), cancellable = true)
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.scrollingText.get()) {
            callback.cancel();
            Minecraft minecraft = Minecraft.getInstance();
            Font font = minecraft.font;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
            RenderSystem.setShaderColor(1, 1, 1, this.alpha);
            int yImage = this.getYImage(this.isHoveredOrFocused());
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            this.blit(stack, this.x, this.y, 0, 46 + yImage * 20, this.width / 2, this.height);
            this.blit(stack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + yImage * 20, this.width / 2, this.height);
            this.renderBg(stack, minecraft, mouseX, mouseY);
            int color = this.getFGColor();
            int padding = Mth.clamp(WidgetConfigs.WIDGET_CONFIGS.buttonTextBorderPadding.get(), 0, this.width / 2 - 1);
            this.renderScrollingString(stack, font, padding, color | Mth.ceil(this.alpha * 255F) << 24);
        }
    }

    @Unique
    public void renderScrollingString(PoseStack stack, Font font, int width, int color) {
        int minX = this.x + width;
        int maxX = this.x + this.width - width;
        this.renderScrollingString(stack, font, this.getMessage(), minX, this.y, maxX, this.y + this.height, color);
    }

    @Inject(method = "getFGColor", at = @At("HEAD"), cancellable = true, remap = false)
    public void getButtonTextColor(CallbackInfoReturnable<Integer> callback) {
        callback.cancel();
        if (this.packedFGColor != UNSET_FG_COLOR) callback.setReturnValue(this.packedFGColor);
        callback.setReturnValue(MellowUtils.getSelectableTextColor(this.isHoveredOrFocused(), this.active));
    }
}
