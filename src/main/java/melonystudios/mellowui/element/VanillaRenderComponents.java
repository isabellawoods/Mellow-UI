package melonystudios.mellowui.element;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.backport.cursor.CursorType;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static melonystudios.mellowui.config.MellowConfigs.CLIENT_CONFIGS;

/// The vanilla ***Render Components***. Contains methods from `AbstractGui` that were updated and/or backported from `GuiGraphics`.
@OnlyIn(Dist.CLIENT)
@SuppressWarnings("deprecation")
public class VanillaRenderComponents extends AbstractGui {
    /// The default instance of vanilla's ***Render Components***.
    public static final VanillaRenderComponents INSTANCE = new VanillaRenderComponents(Minecraft.getInstance());
    private static CursorType currentCursor = CursorTypes.DEFAULT;
    private static CursorType pendingCursor = CursorTypes.DEFAULT;
    protected final Minecraft minecraft;
    protected final MatrixStack stack;

    /// The vanilla ***Render Components***.
    /// Contains methods from `AbstractGui` that were updated and/or backported from `GuiGraphics`.
    /// @param minecraft The *Minecraft* instance class.
    /// @param stack The default {@link MatrixStack} used for rendering.
    protected VanillaRenderComponents(Minecraft minecraft, MatrixStack stack) {
        this.minecraft = minecraft;
        this.stack = stack;
    }

    /// The vanilla **Render Components**.
    /// Contains methods from `AbstractGui` that were updated and/or backported from `GuiGraphics`.
    /// @param minecraft The *Minecraft* instance class.
    protected VanillaRenderComponents(Minecraft minecraft) {
        this(minecraft, new MatrixStack());
    }

    /// @return The {@link MatrixStack} used by this instance of the *Render Components*.
    public MatrixStack matrixStack() {
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
        RenderSystem.color4f(red, green, blue, alpha);
    }

    /// Sets the current rendering color.
    /// @param color The color to set.
    /// @param alpha The alpha component to apply.
    public void setColor(int color, float alpha) {
        float red = ColorHelper.PackedColor.red(color) / 255F;
        float green = ColorHelper.PackedColor.green(color) / 255F;
        float blue = ColorHelper.PackedColor.blue(color) / 255F;
        this.setColor(red, green, blue, alpha);
    }

    /// Requests a certain cursor to be used when hovering over this widget.
    /// @param cursor One of {@linkplain CursorTypes these} cursor types.
    public void requestCursor(CursorType cursor) {
        pendingCursor = cursor;
    }

    /// Applies the selected cursor to the on-screen mouse cursor.
    /// @param window *Minecraft*'s main window, provided by the **game renderer**.
    public void applyCursor(MainWindow window) {
        CursorType cursor = CLIENT_CONFIGS.allowCursorChanges.get() ? pendingCursor : CursorTypes.DEFAULT;
        if (currentCursor != cursor) {
            currentCursor = cursor;
            cursor.select(window);
        }
    }

    /// Draws a **string** at the specified coordinates using the given text and color.
    /// @param text The {@linkplain String string} to render.
    /// @param textShadow Whether the text should have a shadow.
    /// @param x The x-position of the center of the string.
    /// @param y The y-position of the string.
    /// @param color The color of the string.
    public void drawString(String text, boolean textShadow, int x, int y, int color) {
        if (textShadow) this.minecraft.font.drawShadow(this.stack, text, x, y, color);
        else this.minecraft.font.draw(this.stack, text, x, y, color);
    }

    /// Draws a **string** at the specified coordinates using the given text and color.
    /// @param text The {@linkplain ITextComponent text component} to render.
    /// @param textShadow Whether the text should have a shadow.
    /// @param x The x-position of the center of the string.
    /// @param y The y-position of the string.
    /// @param color The color of the string.
    public void drawString(ITextComponent text, boolean textShadow, int x, int y, int color) {
        if (textShadow) this.minecraft.font.drawShadow(this.stack, text, x, y, color);
        else this.minecraft.font.draw(this.stack, text, x, y, color);
    }

    /// Draws a **string** at the specified coordinates using the given text and color.
    /// @param text The {@linkplain IReorderingProcessor formatted text} to render.
    /// @param textShadow Whether the text should have a shadow.
    /// @param x The x-position of the center of the string.
    /// @param y The y-position of the string.
    /// @param color The color of the string.
    public void drawString(IReorderingProcessor text, boolean textShadow, int x, int y, int color) {
        if (textShadow) this.minecraft.font.drawShadow(this.stack, text, x, y, color);
        else this.minecraft.font.draw(this.stack, text, x, y, color);
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
    /// @param text The {@linkplain ITextComponent text component} to render.
    /// @param textShadow Whether the text should have a shadow.
    /// @param x The x-position of the center of the string.
    /// @param y The y-position of the string.
    /// @param color The color of the string.
    public void drawCenteredString(ITextComponent text, boolean textShadow, int x, int y, int color) {
        this.drawAlignedString(text, Alignment.CENTER, textShadow, x, y, color);
    }

    /// Draws a **centered string** at the specified coordinates using the given text and color.
    /// @param text The {@linkplain IReorderingProcessor formatted text} to render.
    /// @param textShadow Whether the text should have a shadow.
    /// @param x The x-position of the center of the string.
    /// @param y The y-position of the string.
    /// @param color The color of the string.
    public void drawCenteredString(IReorderingProcessor text, boolean textShadow, int x, int y, int color) {
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
                this.drawString(text, textShadow, x - this.minecraft.font.width(text), y, color);
                break;
            }
            case CENTER: {
                this.drawString(text, textShadow, x - this.minecraft.font.width(text) / 2, y, color);
                break;
            }
            case LEFT: default: {
                this.drawString(text, textShadow, x, y, color);
                break;
            }
        }
    }

    /// Draws an **aligned string** at the specified coordinates using the given text and color.
    /// @param text The {@linkplain ITextComponent text component} to render.
    /// @param alignment Which side to align the text to.
    /// @param textShadow Whether the text should have a shadow.
    /// @param x The x-position of the center of the string.
    /// @param y The y-position of the string.
    /// @param color The color of the string.
    public void drawAlignedString(ITextComponent text, Alignment alignment, boolean textShadow, int x, int y, int color) {
        switch (alignment) {
            case RIGHT: {
                this.drawString(text, textShadow, x - this.minecraft.font.width(text), y, color);
                break;
            }
            case CENTER: {
                this.drawString(text, textShadow, x - this.minecraft.font.width(text) / 2, y, color);
                break;
            }
            case LEFT: default: {
                this.drawString(text, textShadow, x, y, color);
                break;
            }
        }
    }

    /// Draws an **aligned string** at the specified coordinates using the given text and color.
    /// @param text The {@linkplain IReorderingProcessor formatted text} to render.
    /// @param alignment Which side to align the text to.
    /// @param textShadow Whether the text should have a shadow.
    /// @param x The x-position of the center of the string.
    /// @param y The y-position of the string.
    /// @param color The color of the string.
    public void drawAlignedString(IReorderingProcessor text, Alignment alignment, boolean textShadow, int x, int y, int color) {
        switch (alignment) {
            case RIGHT: {
                this.drawString(text, textShadow, x - this.minecraft.font.width(text), y, color);
                break;
            }
            case CENTER: {
                this.drawString(text, textShadow, x - this.minecraft.font.width(text) / 2, y, color);
                break;
            }
            case LEFT: default: {
                this.drawString(text, textShadow, x, y, color);
                break;
            }
        }
    }
}
