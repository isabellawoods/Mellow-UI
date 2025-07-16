package melonystudios.mellowui.screen.list;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.screen.forge.MUILoadingErrorScreen;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.fml.ModLoadingException;
import net.minecraftforge.fml.ModLoadingWarning;

import javax.annotation.Nonnull;
import java.util.List;

public class LoadingMessageList extends ObjectSelectionList<LoadingMessageList.Message> {
    private final MUILoadingErrorScreen parentScreen;

    public LoadingMessageList(MUILoadingErrorScreen parentScreen, List<ModLoadingException> loadErrors, List<ModLoadingWarning> loadWarnings) {
        super(parentScreen.getMinecraft(), parentScreen.width, parentScreen.height, 32, parentScreen.height - 56, 2 * parentScreen.getMinecraft().font.lineHeight + 8);
        this.parentScreen = parentScreen;
        boolean both = !loadErrors.isEmpty() && !loadWarnings.isEmpty();

        // Load errors
        if (both) this.addEntry(new Message(parentScreen.errorHeader.copy().withStyle(ChatFormatting.UNDERLINE), true));
        loadErrors.forEach(error -> this.addEntry(new Message(new TextComponent(error.formatToString()))));

        // Load warnings
        if (both) this.addEntry(new Message(parentScreen.warningHeader.copy().withStyle(ChatFormatting.UNDERLINE), true));
        loadWarnings.forEach(warning -> this.addEntry(new Message(new TextComponent(warning.formatToString()))));
    }

    @Override
    public int getRowWidth() {
        return 320;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width / 2 + 170;
    }

    public class Message extends ObjectSelectionList.Entry<Message> {
        private final Component component;
        private final boolean header;

        public Message(Component component) {
            this(component, false);
        }

        public Message(Component component, boolean header) {
            this.component = component == null ? TextComponent.EMPTY : component;
            this.header = header;
        }

        @Override
        public void render(PoseStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean mouseOver, float partialTicks) {
            Font font = Minecraft.getInstance().font;
            List<FormattedCharSequence> processors = font.split(this.component, LoadingMessageList.this.getRowWidth());
            int y = top + 2;
            int lineHeight = top + (height / 2);

            for (FormattedCharSequence processor : processors) {
                if (this.header) {
                    int textWidth = font.width(processor);
                    TextColor color = this.component.getStyle().getColor();
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

        @Override
        @Nonnull
        public Component getNarration() {
            return new TranslatableComponent("narrator.select", this.component);
        }
    }
}
