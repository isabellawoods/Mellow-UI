package melonystudios.mellowui.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class ImageSetButton extends Button implements ScrollingText {
    private final WidgetTextureSet textureSet;
    private boolean renderText = false;
    private Alignment alignment = Alignment.CENTER;

    public ImageSetButton(int x, int y, int width, int height, WidgetTextureSet textureSet, IPressable whenPressed, ITextComponent buttonText) {
        super(x, y, width, height, buttonText, whenPressed);
        this.textureSet = textureSet;
    }

    public ImageSetButton(int x, int y, int width, int height, WidgetTextureSet textureSet, IPressable whenPressed, ITooltip buttonTooltip, ITextComponent buttonText) {
        super(x, y, width, height, buttonText, whenPressed, buttonTooltip);
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
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        ResourceLocation iconTexture = this.textureSet.getWidgetTexture(this.isHovered() || this.isFocused(), this.active);

        // Button
        minecraft.getTextureManager().bind(WIDGETS_LOCATION);
        RenderSystem.color4f(1, 1, 1, this.alpha);
        int yImage = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(stack, this.x, this.y, 0, 46 + yImage * 20, this.width / 2, this.height);
        this.blit(stack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + yImage * 20, this.width / 2, this.height);

        // Text
        if (this.renderText) {
            this.renderScrollingString(stack, minecraft.font, 2, this.getFGColor() | MathHelper.ceil(this.alpha * 255F) << 24);
        }

        // Button icon
        minecraft.getTextureManager().bind(iconTexture);
        switch (this.alignment) {
            case RIGHT: {
                blit(stack, this.x, this.y, 0, 0, 20, 20, 20, 20);
                break;
            }
            case LEFT: {
                blit(stack, this.x + this.width - 20, this.y, 0, 0, 20, 20, 20, 20);
                break;
            }
            case CENTER: default: {
                blit(stack, this.x + this.width / 2 - 10, this.y, 0, 0, 20, 20, 20, 20);
            }
        }

        this.renderBg(stack, minecraft, mouseX, mouseY);
        if (this.isHovered()) this.renderToolTip(stack, mouseX, mouseY);
    }

    public void renderScrollingString(MatrixStack stack, FontRenderer font, int width, int color) {
        int minX = this.x + width;
        int maxX = this.x + this.width - width;
        this.renderScrollingString(stack, font, this.getMessage(), minX, this.y, maxX, this.y + this.height, color);
    }

    public enum Alignment {
        LEFT, CENTER, RIGHT
    }
}
