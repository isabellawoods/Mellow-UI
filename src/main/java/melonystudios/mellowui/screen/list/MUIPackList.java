package melonystudios.mellowui.screen.list;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class MUIPackList extends ObjectSelectionList<MUIPackList.PackEntry> {
    public MUIPackList(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight) {
        super(minecraft, width, height, y0, y1, itemHeight);
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.x1 - 6;
    }

    @OnlyIn(Dist.CLIENT)
    public static class PackEntry extends ObjectSelectionList.Entry<PackEntry> {
        protected final Screen screen;
        protected final Minecraft minecraft;
        private final PackSelectionModel.Entry pack;
        private final MUIPackList parent;
        private final FormattedCharSequence nameDisplayCache;
        private final MultiLineLabel descriptionDisplayCache;
        private final FormattedCharSequence incompatibleNameDisplayCache;
        private final MultiLineLabel incompatibleDescriptionDisplayCache;

        public PackEntry(Screen screen, Minecraft minecraft, PackSelectionModel.Entry pack, MUIPackList parent) {
            this.screen = screen;
            this.minecraft = minecraft;
            this.pack = pack;
            this.parent = parent;
            this.nameDisplayCache = cacheName(minecraft, pack.getTitle());
            this.descriptionDisplayCache = cacheDescription(minecraft, pack.getExtendedDescription());
            this.incompatibleNameDisplayCache = cacheName(minecraft, new TranslatableComponent("pack.incompatible"));
            this.incompatibleDescriptionDisplayCache = cacheDescription(minecraft, pack.getCompatibility().getDescription());
        }

        @Override
        public void render(PoseStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hoveringOver, float partialTicks) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, this.pack.getIconTexture());
            RenderSystem.setShaderColor(1, 1, 1, 1);
            GuiComponent.blit(stack, left, top, 0, 0, 32, 32, 32, 32);

            FormattedCharSequence processor = this.nameDisplayCache;
            MultiLineLabel renderer = this.descriptionDisplayCache;

            if (this.showHoverOverlay() && (this.minecraft.options.touchscreen || hoveringOver)) {
                RenderSystem.enableBlend();
                RenderSystem.setShaderTexture(0, GUITextures.PACK_SELECTION_OVERLAY);
                GuiComponent.blit(stack, left, top, 0, 0, 32, 32, 32, 32);
                RenderSystem.disableBlend();
                RenderSystem.setShaderTexture(0, GUITextures.PACK_SELECTION_ICONS);
                RenderSystem.setShaderColor(1, 1, 1, 1);
                int i = mouseX - left;
                int i1 = mouseY - top;
                if (!this.pack.getCompatibility().isCompatible()) {
                    processor = this.incompatibleNameDisplayCache;
                    renderer = this.incompatibleDescriptionDisplayCache;
                }

                if (this.pack.canSelect()) {
                    if (i < 32) {
                        GuiComponent.blit(stack, left, top, 0, 32, 32, 32, 256, 256);
                    } else {
                        GuiComponent.blit(stack, left, top, 0, 0, 32, 32, 256, 256);
                    }
                } else {
                    if (this.pack.canUnselect()) {
                        if (i < 16) {
                            GuiComponent.blit(stack, left, top, 32, 32, 32, 32, 256, 256);
                        } else {
                            GuiComponent.blit(stack, left, top, 32, 0, 32, 32, 256, 256);
                        }
                    }

                    if (this.pack.canMoveUp()) {
                        if (i < 32 && i > 16 && i1 < 16) {
                            GuiComponent.blit(stack, left, top, 96, 32, 32, 32, 256, 256);
                        } else {
                            GuiComponent.blit(stack, left, top, 96, 0, 32, 32, 256, 256);
                        }
                    }

                    if (this.pack.canMoveDown()) {
                        if (i < 32 && i > 16 && i1 > 16) {
                            GuiComponent.blit(stack, left, top, 64, 32, 32, 32, 256, 256);
                        } else {
                            GuiComponent.blit(stack, left, top, 64, 0, 32, 32, 256, 256);
                        }
                    }
                }
            }

            this.minecraft.font.drawShadow(stack, processor, (float) (left + 32 + 2), (float) (top + 1), 0xFFFFFF);
            renderer.renderLeftAligned(stack, left + 32 + 2, top + 12, 10, 0x808080);
        }

        @Override
        @Nonnull
        public Component getNarration() {
            return new TranslatableComponent("narrator.select", this.pack.getTitle());
        }

        private boolean showHoverOverlay() {
            return !this.pack.isFixedPosition() || !this.pack.isRequired();
        }

        private static FormattedCharSequence cacheName(Minecraft minecraft, Component component) {
            int componentWidth = minecraft.font.width(component);
            if (componentWidth > 157) {
                FormattedText textProperties = FormattedText.composite(minecraft.font.substrByWidth(component, 157 - minecraft.font.width("...")), FormattedText.of("..."));
                return Language.getInstance().getVisualOrder(textProperties);
            } else {
                return component.getVisualOrderText();
            }
        }

        private static MultiLineLabel cacheDescription(Minecraft minecraft, Component component) {
            return MultiLineLabel.create(minecraft.font, component, 157, 2);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            double d1 = mouseX - (double) this.parent.getRowLeft();
            double d2 = mouseY - (double) this.parent.getRowTop(this.parent.children().indexOf(this));

            if (this.showHoverOverlay() && d1 <= 32) {
                if (this.pack.canSelect()) {
                    PackCompatibility compatibility = this.pack.getCompatibility();
                    if (compatibility.isCompatible()) {
                        this.pack.select();
                    } else {
                        Component confirmationText = compatibility.getConfirmation();
                        this.minecraft.setScreen(new ConfirmScreen(confirmed -> {
                            this.minecraft.setScreen(this.screen);
                            if (confirmed) this.pack.select();
                        }, new TranslatableComponent("pack.incompatible.confirm.title"), confirmationText));
                    }

                    return true;
                }

                if (d1 < 16 && this.pack.canUnselect()) {
                    this.pack.unselect();
                    return true;
                }

                if (d1 > 16 && d2 < 16 && this.pack.canMoveUp()) {
                    this.pack.moveUp();
                    return true;
                }

                if (d1 > 16 && d2 > 16 && this.pack.canMoveDown()) {
                    this.pack.moveDown();
                    return true;
                }
            }

            return false;
        }
    }
}
