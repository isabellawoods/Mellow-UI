package melonystudios.mellowui.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class ImageSetButton extends Button implements ScrollingText {
    protected final WidgetTextureSet textureSet;
    protected boolean renderText = false;
    protected Alignment alignment = Alignment.CENTER;

    public ImageSetButton(int x, int y, int width, int height, WidgetTextureSet textureSet, OnPress onPress, Component buttonText) {
        super(x, y, width, height, buttonText, onPress);
        this.textureSet = textureSet;
    }

    public ImageSetButton(int x, int y, int width, int height, WidgetTextureSet textureSet, OnPress onPress, OnTooltip buttonTooltip, Component buttonText) {
        super(x, y, width, height, buttonText, onPress, buttonTooltip);
        this.textureSet = textureSet;
    }

    public ImageSetButton renderText(boolean renderText) {
        this.renderText = renderText;
        return this;
    }

    public ImageSetButton alignment(Alignment alignment) {
        this.alignment = alignment;
        return this;
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, this.alpha);

        // Button
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        int yImage = this.getYImage(this.isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(stack, this.x, this.y, 0, 46 + yImage * 20, this.width / 2, this.height);
        this.blit(stack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + yImage * 20, this.width / 2, this.height);

        // Text
        if (this.renderText) {
            this.renderScrollingString(stack, minecraft.font, 2, this.getFGColor() | Mth.ceil(this.alpha * 255F) << 24);
        }

        // Button icon
        RenderSystem.setShaderTexture(0, this.textureSet.getWidgetTexture(this.isHoveredOrFocused(), this.active));
        switch (this.alignment) {
            case RIGHT: {
                blit(stack, this.x + this.width - 20, this.y, 0, 0, 20, 20, 20, 20);
                break;
            }
            case LEFT: {
                blit(stack, this.x, this.y, 0, 0, 20, 20, 20, 20);
                break;
            }
            case CENTER: default: {
                blit(stack, this.x + this.width / 2 - 10, this.y, 0, 0, 20, 20, 20, 20);
            }
        }

        this.renderBg(stack, minecraft, mouseX, mouseY);
        if (this.isFocused()) this.renderToolTip(stack, this.x, this.y);
        else if (this.isHovered) this.renderToolTip(stack, mouseX, mouseY);
    }

    public void renderScrollingString(PoseStack stack, Font font, int width, int color) {
        int minX = this.x + width;
        int maxX = this.x + this.width - width;
        this.renderScrollingString(stack, font, this.getMessage(), minX, this.y, maxX, this.y + this.height, color);
    }

    public enum Alignment {
        LEFT, CENTER, RIGHT
    }
}
