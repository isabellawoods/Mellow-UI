package melonystudios.mellowui.screen.list;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.screen.update.MUIModListScreen;
import melonystudios.mellowui.screen.widget.ScrollingText;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.MavenVersionStringHelper;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import javax.annotation.Nullable;

public class MUIModList extends ExtendedList<MUIModList.Mod> {
    private final MUIModListScreen parentScreen;
    private final int listWidth;

    public MUIModList(MUIModListScreen parentScreen, int width, int height, int y0, int y1, int itemWidth) {
        super(parentScreen.getMinecraft(), width, height, y0, y1, itemWidth);
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
    protected boolean isFocused() {
        return this.parentScreen.getFocused() == this;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.listWidth + 10;
    }

    @Override
    public int getRowWidth() {
        return this.getMaxScroll() > 0 ? this.listWidth : this.listWidth + 10;
    }

    public void refreshModList() {
        this.clearEntries();
        this.parentScreen.loadModList(this::addEntry, mod -> new Mod(this.parentScreen, mod));
    }

    public class Mod extends ExtendedList.AbstractListEntry<MUIModList.Mod> implements ScrollingText {
        private final MUIModListScreen parentScreen;
        private final ModInfo modInfo;

        public Mod(MUIModListScreen parentScreen, ModInfo modInfo) {
            this.parentScreen = parentScreen;
            this.modInfo = modInfo;
        }

        @Override
        public void render(MatrixStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hoveringOver, float partialTicks) {
            ITextComponent modName = new StringTextComponent(this.modInfo.getDisplayName());
            ITextComponent modVersion = new StringTextComponent(MavenVersionStringHelper.artifactVersionToString(this.modInfo.getVersion()));
            VersionChecker.CheckResult checkResult = VersionChecker.getResult(this.modInfo);
            FontRenderer font = this.parentScreen.getMinecraft().font;

            RenderSystem.enableBlend();
            this.parentScreen.getMinecraft().getTextureManager().bind(MUIModList.this.getSelected() == this ? GUITextures.MOD_ENTRY_HIGHLIGHTED : GUITextures.MOD_ENTRY);
            if (MUIModList.this.getMaxScroll() > 0) {
                int rowWidth = MUIModList.this.getRowWidth();
                blit(stack, left - 2, top - 1, 0, 0, rowWidth / 2, 26, rowWidth, 26);
                blit(stack, left - 2 + rowWidth / 2, top - 1, rowWidth - rowWidth / 2F, 0, rowWidth / 2, 26, rowWidth, 26);
            } else {
                int rowWidth = MUIModList.this.getRowWidth() - 6;
                blit(stack, left + 4, top - 1, 0, 0, rowWidth / 2, 26, rowWidth, 26);
                blit(stack, left + 4 + rowWidth / 2, top - 1, rowWidth - rowWidth / 2F, 0, rowWidth / 2, 26, rowWidth, 26);
            }
            RenderSystem.disableBlend();

            int padding = WidgetConfigs.WIDGET_CONFIGS.modNameTextBorderPadding.get();
            this.renderScrollingString(stack, font, modName, left + padding - 2, top, left + width - padding - 2, top + height - 8, MellowUtils.getSelectableTextColor(MUIModList.this.getSelected() == this, true));
            int leftOffset = MUIModList.this.getMaxScroll() > 0 ? 3 : 8;
            font.drawShadow(stack, LanguageMap.getInstance().getVisualOrder(ITextProperties.composite(font.substrByWidth(modVersion, MUIModList.this.listWidth))), left + leftOffset, top + 4 + font.lineHeight, 0xA0A0A0);

            if (checkResult.status.shouldDraw()) {
                MUIModList.this.minecraft.getTextureManager().bind(GUITextures.VERSION_CHECKER_ICONS);
                RenderSystem.color4f(1, 1, 1, 1);
                blit(stack, MUIModList.this.getLeft() + MUIModList.this.width - 12, top + height / 4 + 2, checkResult.status.getSheetOffset() * 8, (checkResult.status.isAnimated() && ((System.currentTimeMillis() / 800 & 1)) == 1 ? 8 : 0), 8, 8, 64, 16);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                this.parentScreen.setSelected(this);
                this.parentScreen.setFocused(this);
                MUIModList.this.setSelected(this);
                MUIModList.this.setFocused(this);
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        public ModInfo getModInformation() {
            return this.modInfo;
        }
    }
}
