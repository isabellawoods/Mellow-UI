package melonystudios.mellowui.screen.list;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.MultiLineLabel;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.resource.panorama.*;
import melonystudios.mellowui.resource.theme.Themes;
import melonystudios.mellowui.screen.MellowCustomizationScreen;
import melonystudios.mellowui.util.Alignment;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@OnlyIn(Dist.CLIENT)
public class PanoramaList extends ExtendedList<PanoramaList.Entry> {
    private final MellowCustomizationScreen parentScreen;
    private final Minecraft minecraft;

    public PanoramaList(Minecraft minecraft, MellowCustomizationScreen parentScreen) {
        super(minecraft, parentScreen.width, parentScreen.height, 24, parentScreen.height - 33, 84);
        this.minecraft = minecraft;
        this.parentScreen = parentScreen;
        this.setRenderSelection(false);

        this.refreshList(parentScreen.search);
        this.centerSelection();
    }

    public void refreshList(String search) {
        this.clearEntries();
        if (!search.isEmpty()) this.setScrollAmount(0);

        Panoramas.PANORAMAS.entrySet().stream()
                .sorted(Comparator.comparing(entry -> new TranslationTextComponent(
                        entry.getKey().getNamespace().equals("generated") ? "panorama.mellowui.generated" : entry.getValue().getDescriptionID())
                        .getString().toLowerCase(Locale.ROOT))
                )
                .forEach(entry -> {
            ResourceLocation texture = Panoramas.cubeMapTexture(entry.getValue(), 0);
            if (!this.minecraft.getResourceManager().hasResource(texture)) return;

            if (!StringUtils.isBlank(search) && search.charAt(0) == '@') {
                String namespace = entry.getKey().getNamespace();
                if (namespace.toLowerCase(Locale.ROOT).contains(search.substring(1).toLowerCase(Locale.ROOT))) {
                    this.addEntry(new Entry(entry.getKey(), entry.getValue()));
                }
            } else {
                String name = new TranslationTextComponent(entry.getKey().getNamespace().equals("generated") ? "panorama.mellowui.generated" : entry.getValue().getDescriptionID()).getString();
                if (StringUtils.isBlank(search) || name.toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT))) {
                    this.addEntry(new Entry(entry.getKey(), entry.getValue()));
                }
            }
        });
        if (TextComponents.isBlank(search)) this.centerSelection();

        // always select the current panorama
        this.children().stream()
                .filter(entry -> entry.assetID.equals(Panoramas.assetID()))
                .findFirst().ifPresent(this::setSelected);
    }

    public void centerSelection() {
        this.children().stream()
                .filter(entry -> entry.assetID.equals(Panoramas.assetID())).findFirst()
                .ifPresent(this::centerScrollOn);
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
        if (entry == null || !this.canSelectPanorama()) return;
        NarratorChatListener.INSTANCE.sayNow(new TranslationTextComponent("narrator.select", new TranslationTextComponent(entry.assetID.getNamespace().equals("generated") ? "panorama.mellowui.generated" : entry.panorama.getDescriptionID())).getString());

        Themes.lastSelectedPanorama = entry.assetID;
        if (!MellowConfigs.CLIENT_CONFIGS.selectedPanorama.get().equals(entry.assetID.toString())) {
            Panoramas.selectPanorama(entry.panorama, entry.assetID.toString());
        }
    }

    public boolean canSelectPanorama() {
        return Themes.theme().panorama() == null;
    }

    @Override
    public int getRowWidth() {
        return 280;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width / 2 + 148;
    }

    @Override
    protected void renderList(MatrixStack stack, int x, int y, int mouseX, int mouseY, float partialTicks) {
        super.renderList(stack, x, y, mouseX, mouseY, partialTicks);
        if (!this.children().isEmpty()) return;
        IFormattableTextComponent translation = new TranslationTextComponent("menu.mellowui.customization.no_panoramas");
        if (!StringUtils.isBlank(this.parentScreen.search) && this.parentScreen.search.length() > 1 && this.parentScreen.search.charAt(0) == '@') {
            translation = new TranslationTextComponent("menu.mellowui.customization.no_panoramas.namespace", this.parentScreen.search.substring(1));
        } else if (!TextComponents.isBlank(this.parentScreen.search)) {
            translation = new TranslationTextComponent("menu.mellowui.customization.no_panoramas.named", this.parentScreen.search);
        }

        List<IReorderingProcessor> lines = this.minecraft.font.split(translation.withStyle(TextComponents.descriptionStyle()), this.width - 50);
        int yOffset = this.height / 2;

        for (IReorderingProcessor line : lines) {
            this.minecraft.font.drawShadow(stack, line, this.width / 2 - this.minecraft.font.width(line) / 2, yOffset, 0xFFFFFF);
            yOffset += this.minecraft.font.lineHeight + 1;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class Entry extends ExtendedList.AbstractListEntry<Entry> {
        private final RenderComponents components = RenderComponents.INSTANCE;
        public final ResourceLocation assetID;
        private final Panorama panorama;

        public Entry(ResourceLocation location, Panorama panorama) {
            this.assetID = location;
            this.panorama = panorama;
        }

        private MultiLineLabel createDescription(IFormattableTextComponent description) {
            return MultiLineLabel.create(PanoramaList.this.minecraft.font, 190, 7, description);
        }

        @Override
        public void render(MatrixStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean mouseOver, float partialTicks) {
            // Render selection
            int color = TextComponents.lockableColor(PanoramaList.this.getSelected() == this, PanoramaList.this.canSelectPanorama());
            if (PanoramaList.this.getSelected() == this) this.components.renderListSelection(left, top, width, height, color);

            RenderSystem.enableBlend();
            RenderSystem.color4f(1, 1, 1, 1);

            // Panorama
            if (!this.panorama.cubeMap().isEmpty()) {
                PanoramaList.this.minecraft.getTextureManager().bind(Panoramas.cubeMapTexture(this.panorama, 0));
                blit(stack, left + 2, top + 2, 0, 0, 76, 76, 76, 76);
            }

            // Panorama Overlay
            PanoramaList.this.minecraft.getTextureManager().bind(Panoramas.overlayTexture(this.panorama));
            blit(stack, left + 2, top + 2, 0, 0, 76, 76, 76, 76);

            // Panorama Frame
            PanoramaList.this.minecraft.getTextureManager().bind(this.getFrameTexture());
            blit(stack, left, top, 0, 0, 80, 80, 80, 80);

            // Panorama speed text
            float speedOverride = this.panorama.speedOverride() == null ? 1 : this.panorama.speedOverride();
            this.components.drawAlignedString(new TranslationTextComponent("menu.mellowui.customization.panorama_speed", speedOverride).withStyle(TextComponents.descriptionStyle()),
                    Alignment.RIGHT, false, left + 77, top + 58 + (this.panorama.pitchOverride() != null ? 0 : 10), 0xFFFFFF | 128 << 24);

            // Panorama pitch
            if (this.panorama.pitchOverride() != null) {
                this.components.drawAlignedString(this.translatePitchOverride().withStyle(TextComponents.descriptionStyle()),
                        Alignment.RIGHT, false, left + 77, top + 68, 0xFFFFFF | 128 << 24);
            }

            // "Locked" text and lock
            if (!PanoramaList.this.canSelectPanorama()) {
                PanoramaList.this.minecraft.getTextureManager().bind(GUITextures.PANORAMA_LOCK);
                blit(stack, left + 25, top + 20, 0, 0, 32, 32, 32, 32);
                this.components.drawCenteredString(new TranslationTextComponent("menu.mellowui.customization.panorama_locked").withStyle(TextFormatting.RED), true, left + 40, top + 53, 0xFFFFFF);
            }
            RenderSystem.disableBlend();

            // Text
            MultiLineLabel description = this.createDescription(this.makePanoramaTooltip());
            if (description != null) description.renderLeftAligned(stack, left + 85, top + 4, 10, 0xFFFFFF);

            // Cursor
            if (PanoramaList.this.canSelectPanorama() && this.isMouseOver(mouseX, mouseY) && this.components.containsPointInScissor(mouseX, mouseY)) {
                this.components.requestCursor(CursorTypes.POINTING_HAND);
            }
        }

        private IFormattableTextComponent translatePitchOverride() {
            PitchOverrider overrider = this.panorama.pitchOverride();
            if (overrider instanceof ConstantPitch) {
                return new TranslationTextComponent("menu.mellowui.customization.panorama.constant_pitch", ((ConstantPitch) overrider).pitch());
            } else if (overrider instanceof BobbingPitch) {
                return new TranslationTextComponent("menu.mellowui.customization.panorama.bobbing_pitch", ((BobbingPitch) overrider).bobbingStrength());
            }
            return null;
        }

        private ResourceLocation getFrameTexture() {
            if (PanoramaList.this.canSelectPanorama()) {
                return PanoramaList.this.minecraft.level != null ? GUITextures.INWORLD_PANORAMA_FRAME : GUITextures.PANORAMA_FRAME;
            } else {
                return PanoramaList.this.minecraft.level != null ? GUITextures.INWORLD_PANORAMA_FRAME_LOCKED : GUITextures.PANORAMA_FRAME_LOCKED;
            }
        }

        private IFormattableTextComponent makePanoramaTooltip() {
            String descriptionID = this.getDescriptionID();
            Style colorStyle = TextComponents.lockableStyle(PanoramaList.this.getSelected() == this, PanoramaList.this.canSelectPanorama());
            IFormattableTextComponent component = new StringTextComponent("");

            // Name ("Default" / "Generated (ID X)")
            if (descriptionID.endsWith("generated")) {
                component.append(new TranslationTextComponent("panorama.mellowui.generated", this.assetID.toString().replace("generated:id_", ""))).withStyle(colorStyle);
            } else {
                component.append(new TranslationTextComponent(descriptionID)).withStyle(colorStyle);
            }

            // Description (if available)
            if (I18n.exists(descriptionID + ".desc")) {
                component.append("\n").append(new TranslationTextComponent(descriptionID + ".desc").withStyle(TextComponents.descriptionStyle()));
            }

            // Post effect ("Shader: Blur")
            if (this.panorama.shader() != null) {
                component.append("\n").append(new TranslationTextComponent("post_effect.panorama",
                        new TranslationTextComponent(Util.makeDescriptionId("post_effect", this.panorama.shader())).withStyle(colorStyle))
                        .withStyle(TextComponents.descriptionStyle()));
            }

            // Asset ID (if advanced tooltips is on) ("mellowui:blur")
            if (PanoramaList.this.minecraft.options.advancedItemTooltips) {
                component.append("\n").append(new StringTextComponent(this.assetID.toString()).withStyle(TextFormatting.DARK_GRAY));
            }
            return component;
        }

        private String getDescriptionID() {
            if (this.assetID.getNamespace().equals("generated")) return "panorama.mellowui.generated";
            return this.panorama.getDescriptionID();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int item) {
            if (item == 0 && PanoramaList.this.canSelectPanorama()) {
                PanoramaList.this.setSelected(this);
                PanoramaList.this.setFocused(this);
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, item);
        }

        @Override
        public int hashCode() {
            return this.assetID.hashCode();
        }
    }
}
