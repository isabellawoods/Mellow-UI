package melonystudios.mellowui.screen.list;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.ScrollingText;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.screen.update.MellowModListScreen;
import melonystudios.mellowui.util.Alignment;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.MavenVersionStringHelper;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import javax.annotation.Nullable;

public class MellowModList extends ExtendedList<MellowModList.Mod> {
    private final MellowModListScreen parentScreen;
    private final int listWidth;

    public MellowModList(MellowModListScreen parentScreen, int width, int height, int y0, int y1, int entryWidth) {
        super(parentScreen.getMinecraft(), width, height, y0, y1, entryWidth);
        this.parentScreen = parentScreen;
        this.listWidth = width;
        this.setRenderSelection(false);
        this.refreshModList();

        if (this.getSelected() != null) this.centerScrollOn(this.getSelected());
    }

    @Override
    public void centerScrollOn(Mod mod) {
        super.centerScrollOn(mod);
    }

    @Override
    public void setSelected(@Nullable Mod mod) {
        super.setSelected(mod);
        this.parentScreen.setSelected(mod);
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
    protected int getScrollbarPosition() {
        return this.listWidth - 6;
    }

    @Override
    public int getRowWidth() {
        return this.listWidth;
    }

    public void refreshModList() {
        this.clearEntries();
        this.parentScreen.loadMods(this::addEntry, mod -> new Mod(this.parentScreen, mod));
    }

    @Nullable
    public Mod byModInfo(ModInfo info) {
        return this.children().stream().filter(entry -> entry.modInfo == info).findFirst().orElse(null);
    }

    public class Mod extends ExtendedList.AbstractListEntry<MellowModList.Mod> implements ScrollingText {
        private final MellowModListScreen parentScreen;
        private final ModInfo modInfo;

        public Mod(MellowModListScreen parentScreen, ModInfo modInfo) {
            this.parentScreen = parentScreen;
            this.modInfo = modInfo;
        }

        public ModInfo getModInformation() {
            return this.modInfo;
        }

        @Override
        public void render(MatrixStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hoveringOver, float partialTicks) {
            ITextComponent modName = new TranslationTextComponent("menu.mellowui.mods.name", this.getModInformation().getDisplayName());
            ITextComponent modVersion = new TranslationTextComponent("menu.mellowui.mods.version", MavenVersionStringHelper.artifactVersionToString(this.getModInformation().getVersion())).withStyle(
                    style -> style.withColor(Color.fromRgb(0xA0A0A0)));
            VersionChecker.CheckResult checkResult = VersionChecker.getResult(this.getModInformation());
            FontRenderer font = this.parentScreen.getMinecraft().font;
            int rowWidth = MellowModList.this.getRowWidth() - (MellowModList.this.getMaxScroll() > 0 ? 6 : 0);

            // Background
            // todo: background looks weird when "list background" is off ~isa 6-12-25
            RenderSystem.enableBlend();
            this.parentScreen.getMinecraft().getTextureManager().bind(MellowModList.this.getSelected() == this ? GUITextures.MOD_ENTRY_HIGHLIGHTED : GUITextures.MOD_ENTRY);
            blit(stack, left - 2, top - 1, 0, 0, rowWidth / 2, 26, rowWidth, 26);
            blit(stack, left - 2 + rowWidth / 2, top - 1, rowWidth - rowWidth / 2F, 0, rowWidth, 26, rowWidth, 26);
            RenderSystem.disableBlend();

            // Mod name
            int padding = WidgetConfigs.WIDGET_CONFIGS.modNameTextPadding.get() - 2;
            int color = TextComponents.selectableColor(MellowModList.this.getSelected() == this, true);
            this.renderWidgetText(
                    () -> this.renderAlignedScrollingText(font, modName, Alignment.CENTER, left + padding, top, left + rowWidth - padding - 4, top + height - 8, color),
                    () -> drawCenteredString(stack, font, modName, left + rowWidth / 2, top + 4, color)
            );

            // Version
            ITextProperties versionComponent = ITextProperties.composite(font.substrByWidth(modVersion, MellowModList.this.listWidth));
            font.drawShadow(stack, LanguageMap.getInstance().getVisualOrder(versionComponent), left + 3, top + 4 + font.lineHeight, 0xFFFFFF);

            // Update available icon
            if (checkResult.status.shouldDraw()) {
                RenderComponents.INSTANCE.renderUpdateAvailableIcon(left + rowWidth - 16, top + height / 4 + 3, 1, checkResult.status);
            }

            // Cursor
            if (this.isMouseOver(mouseX, mouseY)) RenderComponents.INSTANCE.requestCursor(CursorTypes.POINTING_HAND);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            Mod selectedMod = MellowModList.this.getSelected() == this ? null : this;
            if (button == 0) {
                MellowModList.this.setSelected(selectedMod);
                MellowModList.this.setFocused(selectedMod);
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }
}
