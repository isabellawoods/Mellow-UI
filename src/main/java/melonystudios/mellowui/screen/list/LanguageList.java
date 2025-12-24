package melonystudios.mellowui.screen.list;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.screen.update.MUILanguageScreen;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.Language;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Locale;

public class LanguageList extends ExtendedList<LanguageList.Entry> {
    private final MUILanguageScreen parentScreen;

    public LanguageList(MUILanguageScreen parentScreen, int width, int height, int y0, int y1, int itemHeight) {
        super(parentScreen.getMinecraft(), width, height, y0, y1, itemHeight);
        this.parentScreen = parentScreen;
        this.setRenderSelection(false);
        this.refreshList(parentScreen.search);
    }

    public void refreshList(String search) {
        this.clearEntries();
        if (!search.isEmpty()) this.setScrollAmount(0);

        for (Language language : this.parentScreen.languageManager().getLanguages()) {
            Entry entry = new Entry(language);
            if (StringUtils.isBlank(search) || language.toString().toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT))) {
                this.addEntry(entry);
                if (this.parentScreen.languageManager().getSelected().getCode().equals(language.getCode())) this.setSelected(entry);
            }
        }

        if (this.getSelected() != null) this.centerScrollOn(this.getSelected());
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
    public void setSelected(@Nullable Entry entry) {
        super.setSelected(entry);
        if (entry != null) NarratorChatListener.INSTANCE.sayNow(new TranslationTextComponent("narrator.select", entry.language()).getString());
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 50;
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 19;
    }

    public class Entry extends ExtendedList.AbstractListEntry<LanguageList.Entry> {
        private final RenderComponents components = RenderComponents.INSTANCE;
        private final Language language;

        public Entry(Language language) {
            this.language = language;
        }

        public Language language() {
            return this.language;
        }

        @Override
        public void render(MatrixStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hoveringOver, float partialTicks) {
            // Render selection
            int color = TextComponents.lockableColor(LanguageList.this.getSelected() == this, true);
            if (LanguageList.this.getSelected() == this) this.components.renderListSelection(left, top, width, height, color);

            // Text
            this.components.drawCenteredString(new StringTextComponent(this.language().toString()).copy().withStyle(TextComponents.withColor(color)),
                    true, left + width / 2, top + 3, 0xFFFFFF);

            if (this.isMouseOver(mouseX, mouseY) && this.components.containsPointInScissor(mouseX, mouseY)) {
                // Cursor
                this.components.requestCursor(CursorTypes.POINTING_HAND);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int item) {
            if (item == 0) {
                LanguageList.this.setSelected(this);
                LanguageList.this.setFocused(this);
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, item);
        }
    }
}
