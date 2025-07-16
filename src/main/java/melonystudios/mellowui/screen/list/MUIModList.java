package melonystudios.mellowui.screen.list;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.screen.update.MUIModListScreen;
import melonystudios.mellowui.screen.widget.ScrollingText;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.forgespi.language.IModInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MUIModList extends ObjectSelectionList<MUIModList.Mod> {
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
        return this.listWidth + 4;
    }

    @Override
    public int getRowWidth() {
        return this.listWidth;
    }

    public void refreshModList() {
        this.clearEntries();
        this.parentScreen.loadModList(this::addEntry, mod -> new Mod(this.parentScreen, mod));
    }

    public class Mod extends ObjectSelectionList.Entry<MUIModList.Mod> implements ScrollingText {
        private final MUIModListScreen parentScreen;
        private final IModInfo modInfo;

        public Mod(MUIModListScreen parentScreen, IModInfo modInfo) {
            this.parentScreen = parentScreen;
            this.modInfo = modInfo;
        }

        @Override
        public void render(PoseStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hoveringOver, float partialTicks) {
            Component modName = new TranslatableComponent("menu.mellowui.mods.name", this.modInfo.getDisplayName());
            Component modVersion = new TranslatableComponent("menu.mellowui.mods.version", MavenVersionStringHelper.artifactVersionToString(this.modInfo.getVersion())).withStyle(
                    style -> style.withColor(0xA0A0A0));
            VersionChecker.CheckResult checkResult = VersionChecker.getResult(this.modInfo);
            Font font = this.parentScreen.getMinecraft().font;
            int rowWidth = MUIModList.this.getRowWidth() - (MUIModList.this.getMaxScroll() > 0 ? 6 : 0);

            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, MUIModList.this.getSelected() == this ? GUITextures.MOD_ENTRY_HIGHLIGHTED : GUITextures.MOD_ENTRY);
            blit(stack, left - 2, top - 1, 0, 0, rowWidth / 2, 26, rowWidth, 26);
            blit(stack, left - 2 + rowWidth / 2, top - 1, rowWidth - rowWidth / 2F, 0, rowWidth, 26, rowWidth, 26);
            RenderSystem.disableBlend();

            // Mod name
            int padding = WidgetConfigs.WIDGET_CONFIGS.modNameTextBorderPadding.get() - 2;
            this.renderScrollingString(stack, font, modName, left + padding, top, left + rowWidth - padding - 4, top + height - 8,
                    MellowUtils.getSelectableTextColor(MUIModList.this.getSelected() == this, true));

            // Version
            FormattedText versionComponent = FormattedText.composite(font.substrByWidth(modVersion, MUIModList.this.listWidth));
            font.drawShadow(stack, Language.getInstance().getVisualOrder(versionComponent), left + 3, top + 4 + font.lineHeight, 0xFFFFFF);

            if (checkResult.status().shouldDraw()) {
                RenderSystem.setShaderTexture(0, GUITextures.VERSION_CHECKER_ICONS);
                RenderSystem.setShaderColor(1, 1, 1, 1);
                blit(stack, left + rowWidth - 16, top + height / 4 + 2, checkResult.status().getSheetOffset() * 8, (checkResult.status().isAnimated() && ((System.currentTimeMillis() / 800 & 1)) == 1 ? 8 : 0), 8, 8, 64, 16);
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

        public IModInfo getModInformation() {
            return this.modInfo;
        }

        @Override
        @Nonnull
        public Component getNarration() {
            return new TranslatableComponent("narrator.select", this.modInfo.getDisplayName());
        }
    }
}
