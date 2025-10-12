package melonystudios.mellowui.screen.panel;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.util.Alignment;
import melonystudios.mellowui.screen.RenderComponents;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

/// Represents a **panel entry** that renders text aligned to various positions.
public class TextPanelEntry extends PanelEntry {
    private final Alignment alignment;
    private final Component text;
    private int contentHeight;

    /// Represents a **panel entry** that renders text aligned to various positions.
    /// @param panel The parent panel.
    /// @param alignment Where the text should be {@linkplain Alignment aligned}.
    /// @param text The text component to be rendered.
    public TextPanelEntry(Panel panel, Alignment alignment, Component text) {
        super(panel);
        this.alignment = alignment;
        this.text = text;
    }

    /// Represents a **panel entry** that renders text aligned to the right.
    /// @param panel The parent panel.
    /// @param text The text component to be rendered.
    public TextPanelEntry(Panel panel, Component text) {
        this(panel, Alignment.LEFT, text);
    }

    @Override
    public void renderEntry(PoseStack stack, RenderComponents components, int x, int y, int width, int height) {
        Font font = this.panel.getFont();
        List<FormattedCharSequence> lines = font.split(this.text, x * 2 - 16);
        this.contentHeight = lines.size() * font.lineHeight;

        switch (this.alignment) {
            case RIGHT: {
                int yOffset = y;
                for (FormattedCharSequence line : lines) {
                    font.drawShadow(stack, line, (float) (width - x / 2 - font.width(line)), yOffset, 0xFFFFFF);
                    yOffset += font.lineHeight;
                }
            }
            case CENTER: {
                int yOffset = y;
                for (FormattedCharSequence line : lines) {
                    font.drawShadow(stack, line, (float) (x * 2 - font.width(line) / 2), yOffset, 0xFFFFFF);
                    yOffset += font.lineHeight;
                }
                break;
            }
            case LEFT: {
                int yOffset = y;
                for (FormattedCharSequence line : lines) {
                    font.drawShadow(stack, line, x, yOffset, 0xFFFFFF);
                    yOffset += font.lineHeight;
                }
                break;
            }
        }

        super.renderEntry(stack, components, x, y, width, height);
    }

    @Override
    public int getContentHeight() {
        return this.contentHeight;
    }
}
