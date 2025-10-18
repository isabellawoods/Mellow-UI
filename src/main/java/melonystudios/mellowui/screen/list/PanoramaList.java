package melonystudios.mellowui.screen.list;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.resource.panorama.Panorama;
import melonystudios.mellowui.resource.panorama.Panoramas;
import melonystudios.mellowui.screen.MellowCustomizationScreen;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class PanoramaList extends ExtendedList<PanoramaList.Entry> {
    private final MellowCustomizationScreen parentScreen;
    private final Minecraft minecraft;

    public PanoramaList(Minecraft minecraft, MellowCustomizationScreen parentScreen) {
        super(minecraft, parentScreen.width, parentScreen.height, 22, parentScreen.height - 32, 84);
        this.minecraft = minecraft;
        this.parentScreen = parentScreen;

        MellowUtils.PANORAMAS.forEach((key, value) -> {
            ResourceLocation texture = Panoramas.cubeMapTexture(value, 0);
            if (this.minecraft.getResourceManager().hasResource(texture)) {
                this.addEntry(new Entry(key, value));
            }
        });
        this.centerScrollOn(this.children().stream()
                .sorted(Comparator.comparing(entry -> entry.location.getPath()))
                .filter(entry -> entry.location.equals(Panoramas.panoramaLocation())).findFirst()
                .orElse(this.children().get(0)));
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
        if (entry != null) Panoramas.selectPanorama(entry.panorama, entry.location.toString());
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
    public class Entry extends ExtendedList.AbstractListEntry<Entry> {
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
        public void render(MatrixStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean mouseOver, float partialTicks) {
            // Panorama Frame
            RenderSystem.enableBlend();
            RenderSystem.color4f(1, 1, 1, 1);
            PanoramaList.this.minecraft.getTextureManager().bind(PanoramaList.this.minecraft.level != null ? GUITextures.INWORLD_PANORAMA_FRAME : GUITextures.PANORAMA_FRAME);
            blit(stack, left, top, 0, 0, 80, 80, 80, 80);

            // Panorama
            if (!this.panorama.cubeMap().isEmpty()) {
                PanoramaList.this.minecraft.getTextureManager().bind(Panoramas.cubeMapTexture(this.panorama, 0));
                blit(stack, left + 2, top + 2, 0, 0, 76, 76, 76, 76);
            }

            // Panorama Overlay
            PanoramaList.this.minecraft.getTextureManager().bind(Panoramas.overlayTexture(this.panorama));
            blit(stack, left + 2, top + 2, 0, 0, 76, 76, 76, 76);
            RenderSystem.disableBlend();

            // Text
            List<IReorderingProcessor> processors = PanoramaList.this.minecraft.font.split(this.makePanoramaTooltip(), 190);
            int yOffset = top + 4;
            for (IReorderingProcessor processor : processors) {
                PanoramaList.this.minecraft.font.drawShadow(stack, processor, left + 85, yOffset, 0xFFFFFF);
                yOffset += 10;
            }
        }

        private IFormattableTextComponent makePanoramaTooltip() {
            String descriptionID = this.getDescriptionID();
            Style colorStyle = MellowUtils.withColor(MellowUtils.getSelectableTextColor(PanoramaList.this.getSelected() == this, true));
            IFormattableTextComponent component = new StringTextComponent("");

            if (descriptionID.endsWith("generated")) {
                component.append(new TranslationTextComponent("panorama.mellowui.generated", this.location.getPath().substring(this.location.getPath().indexOf('/') + 1)));
            } else {
                component.append(new TranslationTextComponent(descriptionID)).withStyle(colorStyle);
            }
            if (I18n.exists(descriptionID + ".desc")) {
                component.append("\n").append(new TranslationTextComponent(descriptionID + ".desc").withStyle(TextFormatting.GRAY));
            }
            if (this.panorama.shader() != null) {
                component.append("\n").append(new TranslationTextComponent("post_effect.panorama",
                        new TranslationTextComponent(Util.makeDescriptionId("post_effect", this.panorama.shader())).withStyle(colorStyle))
                        .withStyle(TextFormatting.GRAY));
            }
            component.append("\n").append(new StringTextComponent(this.location.toString()).withStyle(TextFormatting.DARK_GRAY));
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
    }
}
