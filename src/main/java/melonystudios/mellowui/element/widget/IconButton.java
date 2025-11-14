package melonystudios.mellowui.element.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.element.WidgetTextureSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class IconButton extends Button {
    private final WidgetTextureSet textureSet;
    private final int textureWidth;
    private final int textureHeight;
    private boolean renderShadow = true;
    private float textAlpha = 0;

    public IconButton(int x, int y, int width, int height, WidgetTextureSet textureSet, ITextComponent text, IPressable whenPressed) {
        super(x, y, width, height, text, whenPressed);
        this.textureSet = textureSet;
        this.textureWidth = width;
        this.textureHeight = height;
    }

    public IconButton(int x, int y, int width, int height, WidgetTextureSet textureSet, ITextComponent text, IPressable whenPressed, ITooltip tooltipText) {
        super(x, y, width, height, text, whenPressed, tooltipText);
        this.textureSet = textureSet;
        this.textureWidth = width;
        this.textureHeight = height;
    }

    public IconButton shouldRenderShadow(boolean renderShadow) {
        this.renderShadow = renderShadow;
        return this;
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bind(this.textureSet.getWidgetTexture(this.isHovered() || this.isFocused(), this.active));

        RenderSystem.color4f(1, 1, 1, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        // Text alpha
        if (this.isFocused() || this.isHovered()) this.textAlpha = MathHelper.clamp(this.textAlpha + 0.15F, 0, 1);
        else this.textAlpha = MathHelper.clamp(this.textAlpha - 0.15F, 0, 1);

        // Icon shadow
        if (this.renderShadow) {
            RenderSystem.color4f(0.25F, 0.25F, 0.25F, this.alpha);
            blit(stack, this.x + 1, this.y + 1, 0, 0, this.width + 1, this.height + 1, this.textureWidth, this.textureHeight);
            RenderSystem.color4f(1, 1, 1, this.alpha);
        }

        // Icon
        blit(stack, this.x, this.y, 0, 0, this.width, this.height, this.textureWidth, this.textureHeight);

        // Text
        int color = this.isFocused() || this.isHovered() ? WidgetConfigs.WIDGET_CONFIGS.highlightedIconButtonColor.get() : WidgetConfigs.WIDGET_CONFIGS.defaultWidgetTextColor.get();
        int alpha = MathHelper.ceil(this.textAlpha * 255) << 24;
        if (this.textAlpha > 0) drawString(stack, minecraft.font, this.getMessage(), this.x - minecraft.font.width(this.getMessage()) - 2, this.y + 2, color | alpha);

        // Tooltip
        if (this.isFocused()) this.renderToolTip(stack, this.x, this.y);
        else if (this.isHovered()) this.renderToolTip(stack, mouseX, mouseY);
    }
}
