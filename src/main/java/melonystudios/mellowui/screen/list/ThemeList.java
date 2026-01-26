package melonystudios.mellowui.screen.list;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.MUIMultiLineLabel;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.IconButton;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.resource.theme.Theme;
import melonystudios.mellowui.resource.theme.Themes;
import melonystudios.mellowui.screen.MellowCustomizationScreen;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@OnlyIn(Dist.CLIENT)
public class ThemeList extends ObjectSelectionList<ThemeList.Entry> {
    private final MellowCustomizationScreen parentScreen;
    private final Minecraft minecraft;

    public ThemeList(Minecraft minecraft, MellowCustomizationScreen parentScreen) {
        super(minecraft, parentScreen.width, parentScreen.height, 24, parentScreen.height - 33, 36);
        this.minecraft = minecraft;
        this.parentScreen = parentScreen;
        this.setRenderSelection(false);
        this.refreshList(parentScreen.search);
        this.centerSelection();
    }

    public void refreshList(String search) {
        this.clearEntries();
        if (!search.isEmpty()) this.setScrollAmount(0);

        Themes.THEMES.entrySet().stream()
                .sorted(Comparator.comparing(entry -> new TranslatableComponent(
                        entry.getValue().getDescriptionID())
                        .getString().toLowerCase(Locale.ROOT))
                )
                .forEach(entry -> {
                    if (!StringUtils.isBlank(search) && search.charAt(0) == '@') {
                        String namespace = entry.getKey().getNamespace();
                        if (namespace.toLowerCase(Locale.ROOT).contains(search.substring(1).toLowerCase(Locale.ROOT))) {
                            this.addEntry(new Entry(entry.getKey(), entry.getValue()));
                        }
                    } else {
                        String name = new TranslatableComponent(entry.getValue().getDescriptionID()).getString();
                        if (StringUtils.isBlank(search) || name.toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT))) {
                            this.addEntry(new Entry(entry.getKey(), entry.getValue()));
                        }
                    }
                });
        if (TextComponents.isBlank(search)) this.centerSelection();

        // always select the current theme
        this.children().stream()
                .filter(entry -> entry.assetID.equals(Themes.assetID()))
                .findFirst().ifPresent(this::setSelected);
    }

    public void centerSelection() {
        this.children().stream()
                .filter(entry -> entry.assetID.equals(Themes.assetID())).findFirst()
                .ifPresent(this::centerScrollOn);
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
    public void setSelected(@javax.annotation.Nullable Entry entry) {
        super.setSelected(entry);
        if (entry == null) return;

        NarratorChatListener.INSTANCE.sayNow(new TranslatableComponent("narrator.select", new TranslatableComponent(entry.theme.getDescriptionID())).getString());
        if (!MellowConfigs.CLIENT_CONFIGS.selectedTheme.get().equals(entry.assetID.toString())) {
            Themes.selectTheme(entry.theme, entry.assetID.toString());
        }
    }

    @Override
    public int getRowWidth() {
        return 310;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width / 2 + 165;
    }

    @Override
    protected void renderList(PoseStack stack, int x, int y, int mouseX, int mouseY, float partialTicks) {
        super.renderList(stack, x, y, mouseX, mouseY, partialTicks);
        if (!this.children().isEmpty()) return;
        MutableComponent translation = new TranslatableComponent("menu.mellowui.customization.no_themes");
        if (!StringUtils.isBlank(this.parentScreen.search) && this.parentScreen.search.length() > 1 && this.parentScreen.search.charAt(0) == '@') {
            translation = new TranslatableComponent("menu.mellowui.customization.no_themes.namespace", this.parentScreen.search.substring(1));
        } else if (!TextComponents.isBlank(this.parentScreen.search)) {
            translation = new TranslatableComponent("menu.mellowui.customization.no_themes.named", this.parentScreen.search);
        }

        List<FormattedCharSequence> lines = this.minecraft.font.split(translation.withStyle(TextComponents.descriptionStyle()), this.width - 50);
        int yOffset = this.height / 2;
        for (FormattedCharSequence line : lines) {
            this.minecraft.font.drawShadow(stack, line, this.width / 2 - this.minecraft.font.width(line) / 2, yOffset, 0xFFFFFF);
            yOffset += this.minecraft.font.lineHeight + 1;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class Entry extends ObjectSelectionList.Entry<Entry> {
        private final RenderComponents components = RenderComponents.INSTANCE;
        public final ResourceLocation assetID;
        private final Theme theme;
        private IconButton applyPacks;
        private IconButton removePacks;

        public Entry(ResourceLocation assetID, Theme theme) {
            this.assetID = assetID;
            this.theme = theme;
            if (!theme.resourcePacks().isEmpty()) {
                WidgetComponents components = WidgetComponents.components(ThemeList.this.parentScreen, widget -> {});
                this.applyPacks = components.enablePacks(button -> Themes.enablePacksFrom(theme), 0, 0);
                this.removePacks = components.disablePacks(button -> Themes.disablePacksFrom(theme), 0, 0);
            }
        }

        private MUIMultiLineLabel createDescription(String description, int maxWidth) {
            if (!I18n.exists(description)) return null;
            return MUIMultiLineLabel.create(ThemeList.this.minecraft.font, maxWidth, 2, new TranslatableComponent(description));
        }

        @Override
        public void render(PoseStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean mouseOver, float partialTicks) {
            // Render selection
            int color = TextComponents.selectableColor(ThemeList.this.getSelected() == this, true);
            if (ThemeList.this.getSelected() == this) this.components.renderListSelection(left, top, width, height, color);

            int textX = left + 32 + 3;
            ResourceLocation themeIcon = Themes.iconLocation(this.assetID);
            if (!ThemeList.this.minecraft.getResourceManager().hasResource(themeIcon)) {
                this.components.setColor(color, 1);
                themeIcon = GUITextures.MISSING_THEME_ICON;
            } else this.components.setColor(1, 1, 1, 1);

            // Icon
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, themeIcon);
            blit(stack, left, top, 0, 0, 32, 32, 32, 32);
            this.components.setColor(1, 1, 1, 1);

            // "Apply Packs" button
            int applyPacksWidth = 0;
            if (this.applyPacks != null) {
                this.applyPacks.active = ThemeList.this.getSelected() == this;
                this.applyPacks.x = left + ThemeList.this.getRowWidth() - 17;
                this.applyPacks.y = top + 3;
                applyPacksWidth = this.applyPacks.isRenderingText() ? ThemeList.this.minecraft.font.width(this.applyPacks.getMessage()) + 1 : 0;
                this.applyPacks.render(stack, mouseX, mouseY, partialTicks);
            }

            // "Remove Packs" button
            int removePacksWidth = 0;
            if (this.removePacks != null) {
                this.removePacks.active = ThemeList.this.getSelected() == this;
                this.removePacks.x = left + ThemeList.this.getRowWidth() - 17;
                this.removePacks.y = top + 17;
                removePacksWidth = this.removePacks.isRenderingText() ? ThemeList.this.minecraft.font.width(this.removePacks.getMessage()) + 1 : 0;
                this.removePacks.render(stack, mouseX, mouseY, partialTicks);
            }

            // Title
            this.components.drawString(new TranslatableComponent(this.theme.getDescriptionID()).withStyle(TextComponents.withColor(color)), true, textX, top + 1, 0xFFFFFF);

            // Description
            int maxWidth = Math.max(applyPacksWidth, removePacksWidth);
            MUIMultiLineLabel description = this.createDescription(this.theme.getDescriptionID() + ".desc", ThemeList.this.getRowWidth() - 3 - 32 - (!this.theme.resourcePacks().isEmpty() ? 14 + maxWidth : 0));
            if (description != null) {
                description.renderLeftAligned(stack, textX, top + 12, 10, WidgetConfigs.WIDGET_CONFIGS.descriptionTextColor.get());
            }

            // Cursor
            if (this.isMouseOver(mouseX, mouseY) && this.components.containsPointInScissor(mouseX, mouseY)) {
                this.components.requestCursor(CursorTypes.POINTING_HAND);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int item) {
            if (this.applyPacks != null && this.applyPacks.mouseClicked(mouseX, mouseY, item)) return true;
            if (this.removePacks != null && this.removePacks.mouseClicked(mouseX, mouseY, item)) return true;

            if (item == 0) {
                ThemeList.this.setSelected(this);
                ThemeList.this.setFocused(this);
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, item);
        }

        @Override
        @NotNull
        public Component getNarration() {
            return new TranslatableComponent("narrator.select", new TranslatableComponent(this.theme.getDescriptionID()));
        }

        @Override
        public int hashCode() {
            return this.assetID.hashCode();
        }
    }
}
