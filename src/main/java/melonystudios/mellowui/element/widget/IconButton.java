package melonystudios.mellowui.element.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.WidgetTextureSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class IconButton extends Button {
    private final WidgetTextureSet textureSet;
    private final int textureWidth;
    private final int textureHeight;
    private float textAlpha = 0;

    public IconButton(int x, int y, int width, int height, WidgetTextureSet textureSet, Component text, OnPress onPress) {
        super(x, y, width, height, text, onPress);
        this.textureSet = textureSet;
        this.textureWidth = width;
        this.textureHeight = height;
    }

    public IconButton(int x, int y, int width, int height, WidgetTextureSet textureSet, Component text, OnPress onPress, OnTooltip tooltipText) {
        super(x, y, width, height, text, onPress, tooltipText);
        this.textureSet = textureSet;
        this.textureWidth = width;
        this.textureHeight = height;
    }

    public boolean isRenderingText() {
        return this.textAlpha > 0;
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.textureSet.getWidgetTexture(this.isHoveredOrFocused(), this.active));
        RenderSystem.setShaderColor(1, 1, 1, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        // Text alpha
        if (this.isHoveredOrFocused() && this.active) this.textAlpha = Mth.clamp(this.textAlpha + 0.15F, 0, 1);
        else this.textAlpha = Mth.clamp(this.textAlpha - 0.15F, 0, 1);

        // Icon
        blit(stack, this.x, this.y, 0, 0, this.width, this.height, this.textureWidth, this.textureHeight);

        // Text
        Minecraft minecraft = Minecraft.getInstance();
        int color = this.isHoveredOrFocused() ? WidgetConfigs.WIDGET_CONFIGS.highlightedIconButtonColor.get() : WidgetConfigs.WIDGET_CONFIGS.defaultWidgetTextColor.get();
        int alpha = Mth.ceil(this.textAlpha * 255F) << 24;
        if (this.textAlpha > 0) drawString(stack, minecraft.font, this.getMessage(), this.x - minecraft.font.width(this.getMessage()) - 2, this.y + 2, color | alpha);

        // Cursor
        if (this.isHovered && this.active) RenderComponents.INSTANCE.requestCursor(CursorTypes.POINTING_HAND);

        // Tooltip
        if (this.isFocused()) this.renderToolTip(stack, this.x, this.y);
        else if (this.isHovered) this.renderToolTip(stack, mouseX, mouseY);
    }
}
