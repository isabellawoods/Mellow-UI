package melonystudios.mellowui.screen.list;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.screen.forge.LoadingErrorsScreen;
import melonystudios.mellowui.util.text.TextComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.ModLoadingException;
import net.minecraftforge.fml.ModLoadingWarning;

import javax.annotation.Nullable;
import java.util.List;

public class LoadingMessageList extends ExtendedList<LoadingMessageList.Message> {
    private static final int ROW_WIDTH = 320;
    private final LoadingErrorsScreen parentScreen;

    public LoadingMessageList(LoadingErrorsScreen parentScreen, List<ModLoadingException> loadErrors, List<ModLoadingWarning> loadWarnings) {
        super(parentScreen.getMinecraft(), parentScreen.width, parentScreen.height, 32, parentScreen.height - 56, Math.max(
                loadErrors.stream().mapToInt(error -> parentScreen.getMinecraft().font.split(new StringTextComponent(error.formatToString()), ROW_WIDTH - 10).size()).max().orElse(0),
                loadWarnings.stream().mapToInt(warning -> parentScreen.getMinecraft().font.split(new StringTextComponent(warning.formatToString()), ROW_WIDTH - 10).size()).max().orElse(0)) *
                parentScreen.getMinecraft().font.lineHeight + 11);
        this.parentScreen = parentScreen;
        boolean both = !loadErrors.isEmpty() && !loadWarnings.isEmpty();

        // Load errors
        if (both) this.addEntry(new Message(parentScreen.errorHeader.copy().withStyle(TextFormatting.UNDERLINE), true));
        loadErrors.forEach(error -> this.addEntry(new Message(new StringTextComponent(error.formatToString()))));

        // Load warnings
        if (both) this.addEntry(new Message(parentScreen.warningHeader.copy().withStyle(TextFormatting.UNDERLINE), true));
        loadWarnings.forEach(warning -> this.addEntry(new Message(new StringTextComponent(warning.formatToString()))));
    }

    @Override
    public void setFocused(@Nullable IGuiEventListener listener) {
        super.setFocused(listener);
        this.parentScreen.setFocused(listener);
    }

    @Override
    protected boolean isFocused() {
        return this.parentScreen.getFocused() == this;
    }

    @Override
    public int getRowWidth() {
        return ROW_WIDTH;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width / 2 + 170;
    }

    public class Message extends ExtendedList.AbstractListEntry<Message> {
        private final ITextComponent component;
        private final boolean header;

        public Message(ITextComponent component) {
            this(component, false);
        }

        public Message(ITextComponent component, boolean header) {
            this.component = component == null ? StringTextComponent.EMPTY : component;
            this.header = header;
        }

        @Override
        public void render(MatrixStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean mouseOver, float partialTicks) {
            FontRenderer font = Minecraft.getInstance().font;
            List<IReorderingProcessor> lines = font.split(this.component, LoadingMessageList.this.getRowWidth() - 10);
            int y = top + 2;
            int lineHeight = top + (height / 2);

            for (IReorderingProcessor processor : lines) {
                if (this.header) {
                    int textWidth = font.width(processor);
                    Color color = this.component.getStyle().getColor();
                    int separatorColor = color != null ? 0xFF000000 + color.getValue() : 0xFFFFFFFF;

                    // left bar
                    fill(stack, left, lineHeight, (left + width / 2) - (textWidth / 2) - 4, lineHeight + 1, separatorColor);
                    fill(stack, left + 1, lineHeight + 1, (left + width / 2) - (textWidth / 2) - 3, lineHeight + 2, TextComponents.darkenColor(color != null ? color.getValue() : 0xFFFFFF, 1, 0.25F));

                    // right bar
                    fill(stack, (left + width / 2) + (textWidth / 2) + 4, lineHeight, left + width - 5, lineHeight + 1, separatorColor);
                    fill(stack, (left + width / 2) + (textWidth / 2) + 5, lineHeight + 1, left + width - 4, lineHeight + 2, TextComponents.darkenColor(color != null ? color.getValue() : 0xFFFFFF, 1, 0.25F));

                    font.drawShadow(stack, processor, left + width / 2 - (textWidth / 2), y + 5, 0xFFFFFF);
                } else {
                    font.drawShadow(stack, processor, left + 5, y, 0xFFFFFF);
                    y += font.lineHeight + 1;
                }
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                LoadingMessageList.this.setSelected(this);
                LoadingMessageList.this.setFocused(this);
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }
}
