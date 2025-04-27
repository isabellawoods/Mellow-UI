package melonystudios.mellowui.screen.list;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.screen.updated.MUIModListScreen;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.MavenVersionStringHelper;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

public class MUIModList extends ExtendedList<MUIModList.Mod> {
    private final MUIModListScreen parentScreen;
    private final int listWidth;

    public MUIModList(MUIModListScreen parentScreen, int width, int height, int y0, int y1, int itemWidth) {
        super(parentScreen.getMinecraft(), width, height, y0, y1, itemWidth);
        this.parentScreen = parentScreen;
        this.listWidth = width;
        this.refreshModList();
    }

    @Override
    protected int getScrollbarPosition() {
        return this.listWidth + 3;
    }

    @Override
    public int getRowWidth() {
        return this.listWidth;
    }

    public void refreshModList() {
        this.clearEntries();
        this.parentScreen.loadModList(this::addEntry, mod -> new Mod(this.parentScreen, mod));
    }

    public class Mod extends ExtendedList.AbstractListEntry<MUIModList.Mod> {
        private final MUIModListScreen parentScreen;
        private final ModInfo modInfo;

        public Mod(MUIModListScreen parentScreen, ModInfo modInfo) {
            this.parentScreen = parentScreen;
            this.modInfo = modInfo;
        }

        @Override
        @SuppressWarnings("deprecation")
        public void render(MatrixStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hoveringOver, float partialTicks) {
            ITextComponent modName = new StringTextComponent(this.modInfo.getDisplayName());
            ITextComponent modVersion = new StringTextComponent(MavenVersionStringHelper.artifactVersionToString(this.modInfo.getVersion()));
            VersionChecker.CheckResult checkResult = VersionChecker.getResult(this.modInfo);
            FontRenderer font = this.parentScreen.getMinecraft().font;

            font.drawShadow(stack, LanguageMap.getInstance().getVisualOrder(ITextProperties.composite(font.substrByWidth(modName, MUIModList.this.listWidth))), left + 3, top + 2, 0xFFFFFF);
            font.drawShadow(stack, LanguageMap.getInstance().getVisualOrder(ITextProperties.composite(font.substrByWidth(modVersion, MUIModList.this.listWidth))), left + 3, top + 2 + font.lineHeight, 0xA0A0A0);

            if (checkResult.status.shouldDraw()) {
                MUIModList.this.minecraft.getTextureManager().bind(GUITextures.VERSION_CHECKER_ICONS);
                RenderSystem.color4f(1, 1, 1, 1);
                blit(stack, MUIModList.this.getLeft() + MUIModList.this.width - 12, top + height / 4 + 2, checkResult.status.getSheetOffset() * 8, (checkResult.status.isAnimated() && ((System.currentTimeMillis() / 800 & 1)) == 1 ? 8 : 0), 8, 8, 64, 16);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            Mod selectedMod = MUIModList.this.getSelected() == this ? null : this;
            this.parentScreen.setSelected(selectedMod);
            MUIModList.this.setSelected(selectedMod);
            return super.mouseClicked(mouseX, mouseY, button);
        }

        public ModInfo getModInformation() {
            return this.modInfo;
        }
    }
}
