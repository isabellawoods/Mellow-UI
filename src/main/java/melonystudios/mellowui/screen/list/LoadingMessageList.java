package melonystudios.mellowui.screen.list;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.screen.forge.MUILoadingErrorScreen;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.ModLoadingException;
import net.minecraftforge.fml.ModLoadingWarning;

import java.util.List;

public class LoadingMessageList extends ExtendedList<LoadingMessageList.Message> {
    private final MUILoadingErrorScreen parentScreen;

    public LoadingMessageList(MUILoadingErrorScreen parentScreen, List<ModLoadingException> loadErrors, List<ModLoadingWarning> loadWarnings) {
        super(parentScreen.getMinecraft(), parentScreen.width, parentScreen.height, 32, parentScreen.height - 56, 2 * parentScreen.getMinecraft().font.lineHeight + 8);
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
    public int getRowWidth() {
        return 320;
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
            List<IReorderingProcessor> processors = font.split(this.component, LoadingMessageList.this.getRowWidth());
            int y = top + 2;
            int lineHeight = top + (height / 2);

            for (IReorderingProcessor processor : processors) {
                if (this.header) {
                    int textWidth = font.width(processor);
                    Color color = this.component.getStyle().getColor();
                    int separatorColor = color != null ? 0xFF000000 + color.getValue() : 0xFFFFFFFF;

                    // left bar
                    fill(stack, left, lineHeight, (left + width / 2) - (textWidth / 2) - 4, lineHeight + 1, separatorColor);
                    fill(stack, left + 1, lineHeight + 1, (left + width / 2) - (textWidth / 2) - 3, lineHeight + 2, MellowUtils.getShadowColor(color != null ? color.getValue() : 0xFFFFFF, 1));

                    // right bar
                    fill(stack, (left + width / 2) + (textWidth / 2) + 4, lineHeight, left + width - 5, lineHeight + 1, separatorColor);
                    fill(stack, (left + width / 2) + (textWidth / 2) + 5, lineHeight + 1, left + width - 4, lineHeight + 2, MellowUtils.getShadowColor(color != null ? color.getValue() : 0xFFFFFF, 1));

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
                LoadingMessageList.this.parentScreen.setFocused(this);
                LoadingMessageList.this.setSelected(this);
                LoadingMessageList.this.setFocused(this);
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }
}
