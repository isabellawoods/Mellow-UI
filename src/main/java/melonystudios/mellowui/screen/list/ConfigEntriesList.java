package melonystudios.mellowui.screen.list;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.ScrollingText;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.screen.EditListConfigScreen;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class ConfigEntriesList extends ObjectSelectionList<ConfigEntriesList.Entry> {
    private final EditListConfigScreen<?> parentScreen;
    private final Minecraft minecraft;

    public ConfigEntriesList(Minecraft minecraft, EditListConfigScreen<?> parentScreen) {
        super(minecraft, parentScreen.width, parentScreen.height, 33, parentScreen.height - 33, 25);
        this.minecraft = minecraft;
        this.parentScreen = parentScreen;

        for (Object entry : parentScreen.getConfig().get()) {
            if (entry instanceof String string) this.addEntry(new StringConfigEntry(string));
        }
        this.addEntry(new AddEntry());
    }

    @Override
    public void setFocused(@Nullable GuiEventListener listener) {
        super.setFocused(listener);
        this.parentScreen.setFocused(listener);
    }

    @Override
    protected boolean isFocused() {
        return this.parentScreen.getFocused() == this;
    }

    @Override
    public int getRowWidth() {
        return 280;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width / 2 + 148;
    }

    @OnlyIn(Dist.CLIENT)
    public abstract class Entry extends ObjectSelectionList.Entry<ConfigEntriesList.Entry> implements ScrollingText {
        protected final Font font = Minecraft.getInstance().font;

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int item) {
            if (item == 0) {
                ConfigEntriesList.this.setSelected(this);
                ConfigEntriesList.this.setFocused(this);
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, item);
        }

        protected void renderString(int x, int y, int width, int height, Alignment alignment, Component text) {
            int padding = 2;
            int minX = x + padding - 2;
            int maxX = x + width - padding - 2;
            int maxY = y + height;
            this.renderAlignedScrollingText(this.font, text, alignment, minX, y, maxX, maxY, 0xFFFFFF);
        }
    }

    private class StringConfigEntry extends Entry {
        private final String entry;

        public StringConfigEntry(String entry) {
            this.entry = entry;
        }

        @Override
        public void render(PoseStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean mouseOver, float partialTicks) {
            Component text = new TextComponent(this.entry).withStyle(TextComponents.selectableStyle(ConfigEntriesList.this.getSelected() == this, true));
            this.renderWidgetText(
                    () -> this.renderString(left, top, width, height, Alignment.LEFT, text),
                    () -> drawCenteredString(stack, this.font, text, width / 2, top - 7, 0xFFFFFF)
            );
        }

        @Override
        @NotNull
        public Component getNarration() {
            return new TranslatableComponent("narrator.select", this.entry);
        }
    }

    private class AddEntry extends Entry {
        @Override
        public void render(PoseStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean mouseOver, float partialTicks) {
            Component text = new TranslatableComponent("button.mellowui.add")
                    .withStyle(TextComponents.selectableStyle(ConfigEntriesList.this.getSelected() == this, true).withItalic(true));
            this.renderWidgetText(
                    () -> this.renderString(left, top, width, height, Alignment.CENTER, text),
                    () -> drawCenteredString(stack, this.font, text, width / 2, top - 7, 0xFFFFFF)
            );
            if (this.isMouseOver(mouseX, mouseY)) RenderComponents.INSTANCE.requestCursor(CursorTypes.POINTING_HAND);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int item) {
            // todo: port this when I have a more "reliable" way of editing configs ~isa 12-10-25
            /*Component configName = ConfigEntriesList.this.parentScreen.getConfigName();
            ValueEntry<String> entry = new ValueEntry<>("", ValueType.STRING, MellowUI.mellowUI("new_entry"));
            EditValueScreen<String> screen = new EditValueScreen<>(ConfigEntriesList.this.parentScreen, configName, entry, false);
            screen.title(new TranslatableComponent("menu.mellowui.add_value.title", configName).withStyle(ChatFormatting.BOLD));
            screen.configSaver(value -> {
                ConfigEntriesList.this.parentScreen.getConfig().get().add(value);
                MellowUI.logger("ConfigEntriesList").debug("tried to save value: {}", value);
            });
            Minecraft.getInstance().setScreen(screen);*/
            return super.mouseClicked(mouseX, mouseY, item);
        }

        @Override
        @NotNull
        public Component getNarration() {
            return new TranslatableComponent("menu.mellowui.add_value.narration");
        }
    }
}
