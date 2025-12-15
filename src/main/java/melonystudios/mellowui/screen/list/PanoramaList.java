package melonystudios.mellowui.screen.list;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.resource.panorama.Panorama;
import melonystudios.mellowui.resource.panorama.Panoramas;
import melonystudios.mellowui.screen.MellowCustomizationScreen;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.*;
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
public class PanoramaList extends ObjectSelectionList<PanoramaList.Entry> {
    private final MellowCustomizationScreen parentScreen;
    private final Minecraft minecraft;

    public PanoramaList(Minecraft minecraft, MellowCustomizationScreen parentScreen) {
        super(minecraft, parentScreen.width, parentScreen.height, 22, parentScreen.height - 32, 84);
        this.minecraft = minecraft;
        this.parentScreen = parentScreen;

        this.refreshList(parentScreen.search);
        this.centerSelection();
    }

    public void refreshList(String search) {
        this.clearEntries();
        if (!search.isEmpty()) this.setScrollAmount(0);

        MellowUtils.PANORAMAS.forEach((location, panorama) -> {
            ResourceLocation texture = Panoramas.cubeMapTexture(panorama, 0);
            if (!this.minecraft.getResourceManager().hasResource(texture)) return;

            if (!StringUtils.isBlank(search) && search.charAt(0) == '@') {
                String namespace = location.getNamespace();
                if (namespace.toLowerCase(Locale.ROOT).contains(search.substring(1).toLowerCase(Locale.ROOT))) {
                    this.addEntry(new Entry(location, panorama));
                }
            } else {
                String name = new TranslatableComponent(location.getNamespace().equals("generated") ? "panorama.mellowui.generated" : panorama.getDescriptionID()).getString();
                if (StringUtils.isBlank(search) || name.toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT))) {
                    this.addEntry(new Entry(location, panorama));
                }
            }
        });
        if (isBlank(search)) this.centerSelection();

        // always select the current panorama
        this.children().stream().filter(entry -> entry.location().equals(MellowConfigs.CLIENT_CONFIGS.selectedPanorama.get()) && this.getSelected() != entry)
                .findFirst().ifPresent(this::setSelected);
    }

    private static boolean isBlank(String search) {
        return StringUtils.isBlank(search) || search.equals("@");
    }

    private void centerSelection() {
        this.children().stream()
                .sorted(Comparator.comparing(entry -> new TranslatableComponent(entry.location.getNamespace().equals("generated") ? "panorama.mellowui.generated" : entry.panorama.getDescriptionID()).getString()))
                .filter(entry -> entry.location.equals(Panoramas.panoramaLocation())).findFirst()
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
    public void setSelected(@Nullable Entry entry) {
        super.setSelected(entry);
        if (entry != null && !MellowConfigs.CLIENT_CONFIGS.selectedPanorama.get().equals(entry.location())) {
            Panoramas.selectPanorama(entry.panorama, entry.location.toString());
        }
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
    protected void renderList(PoseStack stack, int x, int y, int mouseX, int mouseY, float partialTicks) {
        super.renderList(stack, x, y, mouseX, mouseY, partialTicks);
        if (!this.children().isEmpty()) return;
        MutableComponent translation = new TranslatableComponent("menu.mellowui.customization.no_panoramas");
        if (!StringUtils.isBlank(this.parentScreen.search) && this.parentScreen.search.length() > 1 && this.parentScreen.search.charAt(0) == '@') {
            translation = new TranslatableComponent("menu.mellowui.customization.no_panoramas.namespace", this.parentScreen.search.substring(1));
        } else if (!isBlank(this.parentScreen.search)) {
            translation = new TranslatableComponent("menu.mellowui.customization.no_panoramas.named", this.parentScreen.search);
        }

        List<FormattedCharSequence> lines = this.minecraft.font.split(translation.withStyle(TextComponents.descriptionStyle()), this.width - 50);
        int yOffset = this.height / 2;

        for (FormattedCharSequence line : lines) {
            this.minecraft.font.drawShadow(stack, line, this.width / 2 - this.minecraft.font.width(line) / 2, yOffset, 0xFFFFFF);
            yOffset += this.minecraft.font.lineHeight + 1;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class Entry extends ObjectSelectionList.Entry<PanoramaList.Entry> {
        private final ResourceLocation location;
        private final Panorama panorama;

        public Entry(ResourceLocation location, Panorama panorama) {
            this.location = location;
            this.panorama = panorama;
        }

        public String location() {
            return this.location.toString();
        }

        @Override
        public void render(PoseStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean mouseOver, float partialTicks) {
            // Panorama Frame
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, PanoramaList.this.minecraft.level != null ? GUITextures.INWORLD_PANORAMA_FRAME : GUITextures.PANORAMA_FRAME);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            blit(stack, left, top, 0, 0, 80, 80, 80, 80);

            // Panorama
            if (!this.panorama.cubeMap().isEmpty()) {
                RenderSystem.setShaderTexture(0, Panoramas.cubeMapTexture(this.panorama, 0));
                blit(stack, left + 2, top + 2, 0, 0, 76, 76, 76, 76);
            }

            // Panorama Overlay
            RenderSystem.setShaderTexture(0, Panoramas.overlayTexture(this.panorama));
            blit(stack, left + 2, top + 2, 0, 0, 76, 76, 76, 76);
            RenderSystem.disableBlend();

            // Text
            List<FormattedCharSequence> processors = PanoramaList.this.minecraft.font.split(this.makePanoramaTooltip(), 190);
            int yOffset = top + 4;
            for (FormattedCharSequence processor : processors) {
                PanoramaList.this.minecraft.font.drawShadow(stack, processor, left + 85, yOffset, 0xFFFFFF);
                yOffset += 10;
            }

            // Cursor
            if (this.isMouseOver(mouseX, mouseY)) RenderComponents.INSTANCE.requestCursor(CursorTypes.POINTING_HAND);
        }

        private MutableComponent makePanoramaTooltip() {
            String descriptionID = this.getDescriptionID();
            Style colorStyle = TextComponents.selectableStyle(PanoramaList.this.getSelected() == this, true);
            MutableComponent component = new TextComponent("");

            if (descriptionID.endsWith("generated")) {
                component.append(new TranslatableComponent("panorama.mellowui.generated", this.location.toString().replace("generated:id_", "")));
            } else {
                component.append(new TranslatableComponent(descriptionID)).withStyle(colorStyle);
            }
            if (I18n.exists(descriptionID + ".desc")) {
                component.append("\n").append(new TranslatableComponent(descriptionID + ".desc").withStyle(TextComponents.descriptionStyle()));
            }
            if (this.panorama.shader() != null) {
                component.append("\n").append(new TranslatableComponent("post_effect.panorama",
                        new TranslatableComponent(Util.makeDescriptionId("post_effect", this.panorama.shader())).withStyle(colorStyle))
                        .withStyle(TextComponents.descriptionStyle()));
            }
            component.append("\n").append(new TextComponent(this.location.toString()).withStyle(ChatFormatting.DARK_GRAY));
            return component;
        }

        private String getDescriptionID() {
            if (this.location.getNamespace().equals("generated")) return "panorama.mellowui.generated";
            return this.panorama.getDescriptionID();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int item) {
            if (item == 0) {
                PanoramaList.this.setSelected(this);
                PanoramaList.this.setFocused(this);
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, item);
        }

        @Override
        @NotNull
        public Component getNarration() {
            String descriptionID = this.getDescriptionID();
            if (descriptionID.endsWith("generated")) {
                return new TranslatableComponent("narrator.select", new TranslatableComponent("panorama.mellowui.generated", this.location.toString().replace("generated:id_", "")));
            } else {
                return new TranslatableComponent("narrator.select", new TranslatableComponent(descriptionID));
            }
        }
    }
}
