package melonystudios.mellowui.screen.list;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MUIPackLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class MUIPackList extends ExtendedList<MUIPackList.PackEntry> {
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
    public static class PackEntry extends ExtendedList.AbstractListEntry<PackEntry> {
        protected final Screen screen;
        protected final Minecraft minecraft;
        private final MUIPackLoader.IPack pack;
        private MUIPackList parent;
        private final IReorderingProcessor nameDisplayCache;
        private final IBidiRenderer descriptionDisplayCache;
        private final IReorderingProcessor incompatibleNameDisplayCache;
        private final IBidiRenderer incompatibleDescriptionDisplayCache;

        public PackEntry(Screen screen, Minecraft minecraft, MUIPackLoader.IPack pack, MUIPackList parent) {
            this.screen = screen;
            this.minecraft = minecraft;
            this.pack = pack;
            this.parent = parent;
            this.nameDisplayCache = cacheName(minecraft, pack.getTitle());
            this.descriptionDisplayCache = cacheDescription(minecraft, pack.getExtendedDescription());
            this.incompatibleNameDisplayCache = cacheName(minecraft, new TranslationTextComponent("pack.incompatible"));
            this.incompatibleDescriptionDisplayCache = cacheDescription(minecraft, pack.getCompatibility().getDescription());
        }

        @Override
        public void render(MatrixStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hoveringOver, float partialTicks) {
            this.minecraft.getTextureManager().bind(this.pack.getIconTexture());
            RenderSystem.color4f(1, 1, 1, 1);
            AbstractGui.blit(stack, left, top, 0, 0, 32, 32, 32, 32);

            IReorderingProcessor processor = this.nameDisplayCache;
            IBidiRenderer renderer = this.descriptionDisplayCache;

            if (this.showHoverOverlay() && (this.minecraft.options.touchscreen || hoveringOver)) {
                RenderSystem.enableBlend();
                this.minecraft.getTextureManager().bind(GUITextures.PACK_SELECTION_OVERLAY);
                AbstractGui.blit(stack, left, top, 0, 0, 32, 32, 32, 32);
                RenderSystem.disableBlend();
                this.minecraft.getTextureManager().bind(GUITextures.PACK_SELECTION_ICONS);
                RenderSystem.color4f(1, 1, 1, 1);
                int i = mouseX - left;
                int i1 = mouseY - top;
                if (!this.pack.getCompatibility().isCompatible()) {
                    processor = this.incompatibleNameDisplayCache;
                    renderer = this.incompatibleDescriptionDisplayCache;
                }

                if (this.pack.canSelect()) {
                    if (i < 32) {
                        AbstractGui.blit(stack, left, top, 0, 32, 32, 32, 256, 256);
                    } else {
                        AbstractGui.blit(stack, left, top, 0, 0, 32, 32, 256, 256);
                    }
                } else {
                    if (this.pack.canUnselect()) {
                        if (i < 16) {
                            AbstractGui.blit(stack, left, top, 32, 32, 32, 32, 256, 256);
                        } else {
                            AbstractGui.blit(stack, left, top, 32, 0, 32, 32, 256, 256);
                        }
                    }

                    if (this.pack.canMoveUp()) {
                        if (i < 32 && i > 16 && i1 < 16) {
                            AbstractGui.blit(stack, left, top, 96, 32, 32, 32, 256, 256);
                        } else {
                            AbstractGui.blit(stack, left, top, 96, 0, 32, 32, 256, 256);
                        }
                    }

                    if (this.pack.canMoveDown()) {
                        if (i < 32 && i > 16 && i1 > 16) {
                            AbstractGui.blit(stack, left, top, 64, 32, 32, 32, 256, 256);
                        } else {
                            AbstractGui.blit(stack, left, top, 64, 0, 32, 32, 256, 256);
                        }
                    }
                }
            }

            this.minecraft.font.drawShadow(stack, processor, (float) (left + 32 + 2), (float) (top + 1), 0xFFFFFF);
            renderer.renderLeftAligned(stack, left + 32 + 2, top + 12, 10, 0x808080);
        }

        private boolean showHoverOverlay() {
            return !this.pack.isFixedPosition() || !this.pack.isRequired();
        }

        private static IReorderingProcessor cacheName(Minecraft minecraft, ITextComponent component) {
            int componentWidth = minecraft.font.width(component);
            if (componentWidth > 157) {
                ITextProperties textProperties = ITextProperties.composite(minecraft.font.substrByWidth(component, 157 - minecraft.font.width("...")), ITextProperties.of("..."));
                return LanguageMap.getInstance().getVisualOrder(textProperties);
            } else {
                return component.getVisualOrderText();
            }
        }

        private static IBidiRenderer cacheDescription(Minecraft minecraft, ITextComponent component) {
            return IBidiRenderer.create(minecraft.font, component, 157, 2);
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
                        ITextComponent confirmationText = compatibility.getConfirmation();
                        this.minecraft.setScreen(new ConfirmScreen(confirmed -> {
                            this.minecraft.setScreen(this.screen);
                            if (confirmed) this.pack.select();
                        }, new TranslationTextComponent("pack.incompatible.confirm.title"), confirmationText));
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
