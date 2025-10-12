package melonystudios.mellowui.screen.list;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.screen.update.MellowModListScreen;
import melonystudios.mellowui.util.Alignment;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import melonystudios.mellowui.util.text.ScrollingText;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.forgespi.language.IModInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MellowModList extends ObjectSelectionList<MellowModList.Mod> {
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
    public void setFocused(@Nullable GuiEventListener listener) {
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

    public class Mod extends ObjectSelectionList.Entry<MellowModList.Mod> implements ScrollingText {
        private final MellowModListScreen parentScreen;
        private final IModInfo modInfo;

        public Mod(MellowModListScreen parentScreen, IModInfo modInfo) {
            this.parentScreen = parentScreen;
            this.modInfo = modInfo;
        }

        public IModInfo getModInformation() {
            return this.modInfo;
        }

        @Override
        public void render(PoseStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hoveringOver, float partialTicks) {
            Component modName = new TranslatableComponent("menu.mellowui.mods.name", this.getModInformation().getDisplayName());
            Component modVersion = new TranslatableComponent("menu.mellowui.mods.version", MavenVersionStringHelper.artifactVersionToString(this.getModInformation().getVersion())).withStyle(
                    style -> style.withColor(0xA0A0A0));
            VersionChecker.CheckResult checkResult = VersionChecker.getResult(this.getModInformation());
            Font font = this.parentScreen.getMinecraft().font;
            int rowWidth = MellowModList.this.getRowWidth() - (MellowModList.this.getMaxScroll() > 0 ? 6 : 0);

            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, MellowModList.this.getSelected() == this ? GUITextures.MOD_ENTRY_HIGHLIGHTED : GUITextures.MOD_ENTRY);
            blit(stack, left - 2, top - 1, 0, 0, rowWidth / 2, 26, rowWidth, 26);
            blit(stack, left - 2 + rowWidth / 2, top - 1, rowWidth - rowWidth / 2F, 0, rowWidth, 26, rowWidth, 26);
            RenderSystem.disableBlend();

            // Mod name
            int padding = WidgetConfigs.WIDGET_CONFIGS.modNameTextPadding.get() - 2;
            int color = MellowUtils.getSelectableTextColor(MellowModList.this.getSelected() == this, true);
            this.renderWidgetText(
                    () -> this.renderAlignedScrollingText(stack, font, modName, Alignment.CENTER, left + padding, top, left + rowWidth - padding - 4, top + height - 8, color),
                    () -> drawCenteredString(stack, font, modName, left + rowWidth / 2, top + 4, color)
            );

            // Version
            FormattedText versionComponent = FormattedText.composite(font.substrByWidth(modVersion, MellowModList.this.listWidth));
            font.drawShadow(stack, Language.getInstance().getVisualOrder(versionComponent), left + 3, top + 4 + font.lineHeight, 0xFFFFFF);

            if (checkResult.status().shouldDraw()) {
                RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                RenderSystem.setShaderTexture(0, GUITextures.VERSION_CHECKER_ICONS);
                RenderSystem.setShaderColor(1, 1, 1, 1);
                blit(stack, left + rowWidth - 16, top + height / 4 + 2, checkResult.status().getSheetOffset() * 8, (checkResult.status().isAnimated() && ((System.currentTimeMillis() / 800 & 1)) == 1 ? 8 : 0), 8, 8, 64, 16);
            }
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

        @Override
        @NotNull
        public Component getNarration() {
            return new TranslatableComponent("narrator.select", this.getModInformation().getDisplayName());
        }
    }
}
