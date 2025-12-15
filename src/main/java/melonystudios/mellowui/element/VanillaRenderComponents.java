package melonystudios.mellowui.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/// The vanilla ***Render Components***. Contains methods from `GuiComponent` that were updated and/or backported from `GuiGraphics`.
@OnlyIn(Dist.CLIENT)
public class VanillaRenderComponents extends GuiComponent {
    /// The default instance of vanilla's ***Render Components***.
    public static final VanillaRenderComponents INSTANCE = new VanillaRenderComponents(Minecraft.getInstance());
    protected final Minecraft minecraft;
    protected final Font font;
    protected final PoseStack stack;

    /// The vanilla ***Render Components***.
    /// Contains methods from `GuiComponent` that were updated and/or backported from `GuiGraphics`.
    /// @param minecraft The *Minecraft* instance class.
    /// @param stack The default {@link PoseStack} used for rendering.
    protected VanillaRenderComponents(Minecraft minecraft, PoseStack stack) {
        this.minecraft = minecraft;
        this.font = minecraft.font;
        this.stack = stack;
    }

    /// The vanilla **Render Components**.
    /// Contains methods from `GuiComponent` that were updated and/or backported from `GuiGraphics`.
    /// @param minecraft The *Minecraft* instance class.
    protected VanillaRenderComponents(Minecraft minecraft) {
        this(minecraft, new PoseStack());
    }

    /// @return The {@link PoseStack} used by this instance of the *Render Components*.
    public PoseStack poseStack() {
        return this.stack;
    }

    /// Sets the blit offset to manage the rendering order.
    /// @param offset The z-level offset for rendering order.
    @Override
    public void setBlitOffset(int offset) {
        super.setBlitOffset(offset);
    }

    /// Sets the current rendering color.
    /// @param red The red component of the color.
    /// @param green The green component of the color.
    /// @param blue The blue component of the color.
    /// @param alpha The alpha component of the color.
    public void setColor(float red, float green, float blue, float alpha) {
        RenderSystem.setShaderColor(red, green, blue, alpha);
    }

    /// Sets the current rendering color.
    /// @param color The color to set.
    /// @param alpha The alpha component to apply.
    public void setColor(int color, float alpha) {
        float red = FastColor.ARGB32.red(color);
        float green = FastColor.ARGB32.green(color);
        float blue = FastColor.ARGB32.blue(color);
        this.setColor(red, green, blue, alpha);
    }

    /// Draws a **string** at the specified coordinates using the given text and color.
    /// @param text The {@linkplain String string} to render.
    /// @param textShadow Whether the text should have a shadow.
    /// @param x The x-position of the center of the string.
    /// @param y The y-position of the string.
    /// @param color The color of the string.
    public void drawString(String text, boolean textShadow, int x, int y, int color) {
        if (textShadow) this.font.drawShadow(this.stack, text, x, y, color);
        else this.font.draw(this.stack, text, x, y, color);
    }

    /// Draws a **string** at the specified coordinates using the given text and color.
    /// @param text The {@linkplain net.minecraft.network.chat.Component text component} to render.
    /// @param textShadow Whether the text should have a shadow.
    /// @param x The x-position of the center of the string.
    /// @param y The y-position of the string.
    /// @param color The color of the string.
    public void drawString(Component text, boolean textShadow, int x, int y, int color) {
        if (textShadow) this.font.drawShadow(this.stack, text, x, y, color);
        else this.font.draw(this.stack, text, x, y, color);
    }

    /// Draws a **string** at the specified coordinates using the given text and color.
    /// @param text The {@linkplain net.minecraft.util.FormattedCharSequence formatted text} to render.
    /// @param textShadow Whether the text should have a shadow.
    /// @param x The x-position of the center of the string.
    /// @param y The y-position of the string.
    /// @param color The color of the string.
    public void drawString(FormattedCharSequence text, boolean textShadow, int x, int y, int color) {
        if (textShadow) this.font.drawShadow(this.stack, text, x, y, color);
        else this.font.draw(this.stack, text, x, y, color);
    }

    /// Draws a **centered string** at the specified coordinates using the given text and color.
    /// @param text The {@linkplain String string} to render.
    /// @param textShadow Whether the text should have a shadow.
    /// @param x The x-position of the center of the string.
    /// @param y The y-position of the string.
    /// @param color The color of the string.
    public void drawCenteredString(String text, boolean textShadow, int x, int y, int color) {
        this.drawAlignedString(text, Alignment.CENTER, textShadow, x, y, color);
    }

    /// Draws a **centered string** at the specified coordinates using the given text and color.
    /// @param text The {@linkplain Component text component} to render.
    /// @param textShadow Whether the text should have a shadow.
    /// @param x The x-position of the center of the string.
    /// @param y The y-position of the string.
    /// @param color The color of the string.
    public void drawCenteredString(Component text, boolean textShadow, int x, int y, int color) {
        this.drawAlignedString(text, Alignment.CENTER, textShadow, x, y, color);
    }

    /// Draws a **centered string** at the specified coordinates using the given text and color.
    /// @param text The {@linkplain FormattedCharSequence formatted text} to render.
    /// @param textShadow Whether the text should have a shadow.
    /// @param x The x-position of the center of the string.
    /// @param y The y-position of the string.
    /// @param color The color of the string.
    public void drawCenteredString(FormattedCharSequence text, boolean textShadow, int x, int y, int color) {
        this.drawAlignedString(text, Alignment.CENTER, textShadow, x, y, color);
    }

    /// Draws an **aligned string** at the specified coordinates using the given text and color.
    /// @param text The {@linkplain String string} to render.
    /// @param alignment Which side to align the text to.
    /// @param textShadow Whether the text should have a shadow.
    /// @param x The x-position of the center of the string.
    /// @param y The y-position of the string.
    /// @param color The color of the string.
    public void drawAlignedString(String text, Alignment alignment, boolean textShadow, int x, int y, int color) {
        switch (alignment) {
            case RIGHT: {
                this.drawString(text, textShadow, x - this.font.width(text), y, color);
                break;
            }
            case CENTER: {
                this.drawString(text, textShadow, x - this.font.width(text) / 2, y, color);
                break;
            }
            case LEFT: default: {
                this.drawString(text, textShadow, x, y, color);
                break;
            }
        }
    }

    /// Draws an **aligned string** at the specified coordinates using the given text and color.
    /// @param text The {@linkplain Component text component} to render.
    /// @param alignment Which side to align the text to.
    /// @param textShadow Whether the text should have a shadow.
    /// @param x The x-position of the center of the string.
    /// @param y The y-position of the string.
    /// @param color The color of the string.
    public void drawAlignedString(Component text, Alignment alignment, boolean textShadow, int x, int y, int color) {
        switch (alignment) {
            case RIGHT: {
                this.drawString(text, textShadow, x - this.font.width(text), y, color);
                break;
            }
            case CENTER: {
                this.drawString(text, textShadow, x - this.font.width(text) / 2, y, color);
                break;
            }
            case LEFT: default: {
                this.drawString(text, textShadow, x, y, color);
                break;
            }
        }
    }

    /// Draws an **aligned string** at the specified coordinates using the given text and color.
    /// @param text The {@linkplain FormattedCharSequence formatted text} to render.
    /// @param alignment Which side to align the text to.
    /// @param textShadow Whether the text should have a shadow.
    /// @param x The x-position of the center of the string.
    /// @param y The y-position of the string.
    /// @param color The color of the string.
    public void drawAlignedString(FormattedCharSequence text, Alignment alignment, boolean textShadow, int x, int y, int color) {
        switch (alignment) {
            case RIGHT: {
                this.drawString(text, textShadow, x - this.font.width(text), y, color);
                break;
            }
            case CENTER: {
                this.drawString(text, textShadow, x - this.font.width(text) / 2, y, color);
                break;
            }
            case LEFT: default: {
                this.drawString(text, textShadow, x, y, color);
                break;
            }
        }
    }
}
